package com.zhangke.framework.utils

import com.eygraber.uri.Uri

typealias PlatformUri = Uri

fun String.toPlatformUri(): PlatformUri {
    return Uri.parse(this)
}
