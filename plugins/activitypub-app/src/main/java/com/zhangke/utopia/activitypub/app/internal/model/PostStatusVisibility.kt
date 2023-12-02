package com.zhangke.utopia.activitypub.app.internal.model

import com.zhangke.activitypub.entities.ActivityPubStatusVisibilityEntity
import com.zhangke.utopia.activitypub.app.R

enum class PostStatusVisibility {

    PUBLIC,
    UNLISTED,
    FOLLOWERS_ONLY,
    MENTIONS_ONLY;

    val describeStringId: Int
        get() = when (this) {
            PUBLIC -> R.string.post_status_scope_public
            UNLISTED -> R.string.post_status_scope_unlisted
            FOLLOWERS_ONLY -> R.string.post_status_scope_follower_only
            MENTIONS_ONLY -> R.string.post_status_scope_mentioned_only
        }

    fun toEntity() = when (this) {
        PUBLIC -> ActivityPubStatusVisibilityEntity.PUBLIC
        UNLISTED -> ActivityPubStatusVisibilityEntity.UNLISTED
        FOLLOWERS_ONLY -> ActivityPubStatusVisibilityEntity.PRIVATE
        MENTIONS_ONLY -> ActivityPubStatusVisibilityEntity.DIRECT
    }

    companion object {

        fun fromEntity(entity: ActivityPubStatusVisibilityEntity) = when (entity) {
            ActivityPubStatusVisibilityEntity.PUBLIC -> PUBLIC
            ActivityPubStatusVisibilityEntity.UNLISTED -> UNLISTED
            ActivityPubStatusVisibilityEntity.PRIVATE -> FOLLOWERS_ONLY
            ActivityPubStatusVisibilityEntity.DIRECT -> MENTIONS_ONLY
        }
    }
}
