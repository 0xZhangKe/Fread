package com.zhangke.framework.network

import java.net.URL

class FormalBaseUrl private constructor(
    val scheme: String,
    val host: String,
) {

    override fun toString(): String {
        return "$scheme$SCHEME_SEPARATOR$host"
    }

    companion object {

        private const val DEFAULT_SCHEME = "https"
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
