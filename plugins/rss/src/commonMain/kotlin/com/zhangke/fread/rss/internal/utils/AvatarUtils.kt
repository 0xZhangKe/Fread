package com.zhangke.fread.rss.internal.utils

import com.zhangke.fread.rss.internal.model.RssSource


expect object AvatarUtils {
    fun isRemoteAvatar(avatar: String?): Boolean

    fun makeSourceAvatar(source: RssSource): String?
}
