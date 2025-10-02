package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubStatusVisibilityEntity
import com.zhangke.fread.status.model.StatusVisibility

fun StatusVisibility.toEntityVisibility(): ActivityPubStatusVisibilityEntity {
    return when (this) {
        StatusVisibility.PUBLIC -> ActivityPubStatusVisibilityEntity.PUBLIC
        StatusVisibility.UNLISTED -> ActivityPubStatusVisibilityEntity.UNLISTED
        StatusVisibility.PRIVATE -> ActivityPubStatusVisibilityEntity.PRIVATE
        StatusVisibility.DIRECT -> ActivityPubStatusVisibilityEntity.DIRECT
    }
}

fun String.toStatusVisibility(): StatusVisibility {
    return when (this) {
        ActivityPubStatusVisibilityEntity.PUBLIC.code -> StatusVisibility.PUBLIC
        ActivityPubStatusVisibilityEntity.UNLISTED.code -> StatusVisibility.UNLISTED
        ActivityPubStatusVisibilityEntity.PRIVATE.code -> StatusVisibility.PRIVATE
        ActivityPubStatusVisibilityEntity.DIRECT.code -> StatusVisibility.DIRECT
        else -> throw IllegalArgumentException("Unknown visibility code: $this")
    }
}
