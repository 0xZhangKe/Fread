package com.zhangke.utopia.activitypub.app.internal.utils

import android.util.Base64

internal fun String.encodeToBase64(): String {
    return String(Base64.encode(this.toByteArray(), Base64.NO_WRAP))
}

internal fun String.decodeFromBase64(): String {
    return String(Base64.decode(this, Base64.NO_WRAP))
}