package com.zhangke.framework.network

object HttpScheme {

    const val HTTP = "http://"
    const val HTTPS = "https://"
}

fun String.addProtocolIfNecessary(): String {
    if (this.contains("://")) return this
    return "${HttpScheme.HTTPS}$this"
}
