package com.zhangke.framework.network

object HttpScheme {

    const val HTTP = "http://"
    const val HTTPS = "https://"

    fun validate(scheme: String): Boolean {
        val fixedScheme = scheme.lowercase()
        return fixedScheme == HTTP || fixedScheme == HTTPS
    }
}

fun String.addProtocolIfNecessary(): String {
    if (this.contains("://")) return this
    return "${HttpScheme.HTTPS}$this"
}

fun String.addProtocolSuffixIfNecessary(): String {
    if (this.endsWith("://")) return this
    return "${this}://"
}
