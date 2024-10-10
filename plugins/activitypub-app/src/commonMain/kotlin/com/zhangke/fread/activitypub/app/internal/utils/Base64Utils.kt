package com.zhangke.fread.activitypub.app.internal.utils

import io.ktor.utils.io.core.toByteArray
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

// FIXME: Support Base64.NO_WRAP
@OptIn(ExperimentalEncodingApi::class)
internal fun String.encodeToBase64(): String {
    return Base64.UrlSafe.encode(this.toByteArray())
}

// FIXME: Support Base64.NO_WRAP
@OptIn(ExperimentalEncodingApi::class)
internal fun String.decodeFromBase64(): String {
    return Base64.UrlSafe.decode(this).decodeToString()
}