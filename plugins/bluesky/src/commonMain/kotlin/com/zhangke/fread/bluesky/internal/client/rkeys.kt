package com.zhangke.fread.bluesky.internal.client

import com.zhangke.fread.status.status.model.Status
import sh.christian.ozone.api.RKey

internal val Status.rkey: RKey
    get() = intrinsicBlog.url.adjustToRkey()

internal fun String.adjustToRkey(): RKey = RKey(this.substringAfterLast("/"))

internal val selfRkey: RKey = RKey("self")
