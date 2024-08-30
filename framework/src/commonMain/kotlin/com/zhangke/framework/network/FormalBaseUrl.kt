package com.zhangke.framework.network

import com.zhangke.framework.utils.Parcelize
import com.zhangke.framework.utils.PlatformParcelable
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.framework.utils.uriString
import io.ktor.http.encodeURLPath
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
class FormalBaseUrl private constructor(
    val scheme: String,
    val host: String,
) : PlatformParcelable, PlatformSerializable {

    override fun toString(): String {
        return "$scheme$SCHEME_SEPARATOR$host"
    }

    override fun hashCode(): Int {
        var result = scheme.hashCode()
        result = 31 * result + host.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other == null) return false
        if (other !is FormalBaseUrl) return false
        return (other.scheme == scheme) && (other.host == host)
    }

    fun equalsDomain(other: FormalBaseUrl): Boolean {
        if (this == other) return true
        if (this.host.endsWith(other.host)) return true
        if (other.host.endsWith(this.host)) return true
        return false
    }

    /**
     * Not encode string
     */
    fun toRawString(): String {
        return uriString(
            scheme = scheme,
            host = host,
            path = "",
            queries = emptyMap(),
            encode = false,
        )
    }

    companion object {

        private const val SCHEME_SEPARATOR = "://"

        fun build(scheme: String, host: String): FormalBaseUrl {
            return FormalBaseUrl(scheme, host)
        }

        fun parse(string: String): FormalBaseUrl? {
            val url = SimpleUri.parse(string.addProtocolIfNecessary()) ?: return null
            val scheme = url.scheme?.lowercase() ?: return null
            if (scheme !in arrayOf("http", "https")) return null
            val host = url.host ?: return null
            if (host.isEmpty()) return null
            return FormalBaseUrl(scheme, host.removeHostSuffix())
        }

        private fun String.removeHostSuffix(): String {
            return this.removeSuffix("/")
        }
    }
}

fun FormalBaseUrl.encode(): String {
    return this.toRawString().encodeURLPath()
}
