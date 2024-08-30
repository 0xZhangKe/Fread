package com.zhangke.framework.utils

import android.net.Uri
import androidx.core.net.toUri

fun PlatformUri.toAndroidUri(): Uri = toString().toUri()
