package com.zhangke.framework.network

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.net.URL

@Parcelize
@Serializable
class FormalBaseUrl private constructor(
    val scheme: String,
    val host: String,
) : Parcelable {

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

    companion object {

        private const val SCHEME_SEPARATOR = "://"

        fun build(scheme: String, host: String): FormalBaseUrl {
            return FormalBaseUrl(scheme, host)
        }

        fun parse(string: String): FormalBaseUrl? {
            val url = try {
                URL(string.addProtocolIfNecessary())
            } catch (e: Throwable) {
                return null
            }
            return FormalBaseUrl(url.protocol, url.host.removeHostSuffix())
        }

        private fun String.removeHostSuffix(): String {
            return this.removeSuffix("/")
        }
    }
}
