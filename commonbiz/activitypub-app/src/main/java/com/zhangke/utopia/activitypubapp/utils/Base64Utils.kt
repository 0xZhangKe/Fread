package com.zhangke.utopia.activitypubapp.utils

import android.util.Base64

internal fun String.encodeToBase64(): String {
    return Base64.encodeToString(this.toByteArray(), Base64.DEFAULT)
}

internal fun String.decodeFromBase64(): String{
    return Base64.decode(this, Base64.DEFAULT).toString()
}