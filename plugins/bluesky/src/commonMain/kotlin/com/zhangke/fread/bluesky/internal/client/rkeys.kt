package com.zhangke.fread.bluesky.internal.client

import com.zhangke.fread.status.status.model.Status

internal val Status.rkey: String
    get() = intrinsicBlog.url.substringAfterLast("/")

internal const val selfRkey: String = "self"
