package com.zhangke.fread.bluesky.internal.client

import com.zhangke.fread.status.status.model.Status

internal val Status.rkey: String
    get() = intrinsicBlog.url.adjustToRkey()

internal fun String.adjustToRkey(): String = this.substringAfterLast("/")

internal const val selfRkey: String = "self"
