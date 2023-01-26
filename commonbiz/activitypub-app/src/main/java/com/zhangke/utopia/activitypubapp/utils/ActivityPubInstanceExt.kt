package com.zhangke.utopia.activitypubapp.utils

import com.zhangke.activitypub.entry.ActivityPubInstance
import com.zhangke.utopia.blogprovider.MetaSourceInfo

internal fun ActivityPubInstance.toMetaSource(): MetaSourceInfo {
    return MetaSourceInfo(
        url = domain,
        name = title,
        thumbnail = thumbnail.url,
        description = description,
        extra = null
    )
}