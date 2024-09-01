package com.zhangke.fread.activitypub.app.internal.usecase.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.fread.status.status.model.StatusInteraction
import me.tatarka.inject.annotations.Inject

class GetStatusInteractionUseCase @Inject constructor() {

    operator fun invoke(
        entity: ActivityPubStatusEntity,
        isSelfStatus: Boolean,
        logged: Boolean,
    ): List<StatusInteraction> {
        return getStatusInteraction(
            entity = entity.reblog ?: entity,
            isSelfStatus = isSelfStatus,
            logged = logged,
        )
    }

    private fun getStatusInteraction(
        entity: ActivityPubStatusEntity,
        isSelfStatus: Boolean,
        logged: Boolean,
    ): List<StatusInteraction> {
        val actionList = mutableListOf<StatusInteraction>()
        actionList += StatusInteraction.Like(
            likeCount = entity.favouritesCount,
            liked = entity.favourited ?: false,
            enable = logged,
        )
        actionList += StatusInteraction.Forward(
            forwardCount = entity.reblogsCount,
            forwarded = entity.reblogged ?: false,
            enable = logged,
        )
        actionList += StatusInteraction.Comment(
            commentCount = entity.repliesCount,
            enable = logged,
        )
        actionList += StatusInteraction.Bookmark(
            bookmarkCount = null,
            bookmarked = entity.bookmarked ?: false,
            enable = logged,
        )
        actionList += StatusInteraction.Bookmark(
            bookmarkCount = null,
            bookmarked = entity.bookmarked ?: false,
            enable = true,
        )
        if (isSelfStatus) {
            actionList.add(StatusInteraction.Delete(enable = true))
            val pinned = entity.pinned == true
            actionList.add(StatusInteraction.Pin(pinned = pinned, enable = true))
        }
        return actionList
    }
}
