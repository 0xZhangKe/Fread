package com.zhangke.framework.utils

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.HttpScheme
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.encodeURLPath
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Supported:
 * - jw@jakewharton.com
 * - @jw@jakewharton.com
 * - acct:@jw@jakewharton.com
 * - https://m.cmx.im/@jw@jakewharton.com
 * - https://m.cmx.im/@AtomZ
 * - m.cmx.im/@jw@jakewharton.com
 * - jakewharton.com/@jw
 *
 * For Bluesky Workaround: @bsky@did
 * name is bsky
 * host is did
 */
@Parcelize
@Serializable
class WebFinger private constructor(
    val name: String,
    val host: String,
) : PlatformParcelable, PlatformSerializable {

    val did: String? = if (name == NAME_DID) host else null

    override fun toString(): String {
        return "@$name@$host"
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + host.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null) return false
        if (other !is WebFinger) return false
        return (other.name == name) && (other.host == host)
    }

    fun equalsDomain(other: WebFinger): Boolean {
        if (this == other) return true
        if (this.name != other.name) return false
        if (this.host.endsWith(other.host)) return true
        if (other.host.endsWith(this.host)) return true
        return false
    }

    companion object {

        private const val NAME_DID = "did"

        fun create(content: String, baseUrl: FormalBaseUrl? = null): WebFinger? {
            if (content.isBlank()) return null
            return createAsAcct(content, baseUrl) ?: createAsUrl(content)
        }

        fun build(name: String, host: String): WebFinger {
            return WebFinger(name, host)
        }

        fun createFromDid(did: String): WebFinger {
            return WebFinger(NAME_DID, did)
        }

        fun decodeFromUrlString(text: String): WebFinger? {
            return try {
                text.decodeURLQueryComponent().let {
                    Json.decodeFromString(serializer(), it)
                }
            } catch (e: Throwable) {
                null
            }
        }

        private fun createAsAcct(content: String, baseUrl: FormalBaseUrl? = null): WebFinger? {
            val fixedAcct = content.removePrefix("acct:").removePrefix("@")
            val name: String
            val host: String
            val split = fixedAcct.split('@')
            if (split.size == 1 && baseUrl != null) {
                name = split[0]
                host = baseUrl.host
            } else if (split.size == 2) {
                name = split[0]
                host = split[1]
            } else {
                return null
            }
            if (!hostValidate(host)) return null
            return WebFinger(name, host)
        }

        private fun createAsUrl(content: String): WebFinger? {
            val maybeUrl = content
                .removePrefix(HttpScheme.HTTP)
                .removePrefix(HttpScheme.HTTPS)
            val split = maybeUrl.split('/')
            if (split.size != 2) return null
            val urlHost = split[0]
            if (!hostValidate(urlHost)) return null
            val maybeAcct = split[1].removePrefix("@")
            return if (maybeAcct.contains('@')) {
                createAsAcct(maybeAcct)
            } else {
                createAsAcct("$maybeAcct@$urlHost")
            }
        }

        private fun hostValidate(host: String): Boolean {
            return RegexFactory.domainRegex.matches(host)
        }
    }
}

fun WebFinger.encodeToUrlString(): String {
    return Json.encodeToString(WebFinger.serializer(), this).encodeURLPath()
}
