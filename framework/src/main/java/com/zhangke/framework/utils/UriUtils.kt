//package com.zhangke.framework.utils
//
//import android.net.Uri
//import androidx.core.net.toUri
//
//fun uriString(baseUrl: String, vararg query: String): String{
//    if (query.isEmpty()) return baseUrl
//    if (query.firstOrNull { it.isNotEmpty() } == null) return baseUrl
//    val builder = StringBuilder()
//    val fixedBaseUrl = baseUrl
//    if (baseUrl.endsWith("/"))
//    return query.joinToString(prefix = baseUrl, separator = "&")
//}
