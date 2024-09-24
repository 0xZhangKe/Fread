package com.zhangke.framework.utils

import android.net.Uri
import androidx.core.net.toUri
import com.eygraber.uri.toUri

fun PlatformUri.toAndroidUri(): Uri = toString().toUri()

fun Uri.toPlatformUri(): PlatformUri = toUri()