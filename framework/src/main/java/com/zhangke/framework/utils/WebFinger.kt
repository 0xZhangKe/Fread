package com.zhangke.framework.utils

import android.os.Parcelable
import com.zhangke.framework.network.HttpScheme
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * Supported:
 * - jw@jakewharton.com
 * - @jw@jakewharton.com
 * - acct:@jw@jakewharton.com
 * - https://m.cmx.im/@jw@jakewharton.com
 * - https://m.cmx.im/@AtomZ
 * - m.cmx.im/@jw@jakewharton.com
 * - jakewharton.com/@jw
 */

@Parcelize
@Serializable
class WebFinger private constructor(
    val name: String,
    val host: String,
) : Parcelable {

    override fun toString(): String {
        return "@$name@$host"
    }

    override fun hashCode(): Int {
        return name.hashCode() + host.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is WebFinger) return false
        return (other.name == name) && (other.host == host)
    }

    companion object {

        fun create(content: String): WebFinger? {
            if (content.isBlank()) return null
            return createAsAcct(content) ?: createAsUrl(content)
        }

        private fun createAsAcct(content: String): WebFinger? {
            val maybeAcct = content
                .removePrefix("acct:")
                .removePrefix("@")
            val split = maybeAcct.split('@')
            if (split.size != 2) return null
            val name = split[0]
            val host = split[1]
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
            return RegexFactory.getDomainRegex().matches(host)
        }
    }
}