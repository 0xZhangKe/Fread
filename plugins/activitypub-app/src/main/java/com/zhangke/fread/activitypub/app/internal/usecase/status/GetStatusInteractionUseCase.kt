package com.zhangke.fread.activitypub.app.internal.usecase.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.StatusInteraction
import javax.inject.Inject

class GetStatusInteractionUseCase @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val loggedAccountAdapter: ActivityPubLoggedAccountAdapter,
) {

    suspend operator fun invoke(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform,
    ): List<StatusInteraction> {
        return getStatusInteraction(entity.reblog ?: entity, platform)
    }

    private suspend fun getStatusInteraction(
        entity: ActivityPubStatusEntity,
        platform: BlogPlatform,
    ): List<StatusInteraction> {
        val account = accountManager.getAllLoggedAccount()
            .firstOrNull { it.platform.uri == platform.uri }
        val statusAuthorWebFinger = loggedAccountAdapter.accountToWebFinger(entity.account)
        val isSelfStatus = account?.webFinger == statusAuthorWebFinger
        val hasActiveUser = account != null
        val actionList = mutableListOf<StatusInteraction>()
        actionList += StatusInteraction.Like(
            likeCount = entity.favouritesCount,
            liked = entity.favourited ?: false,
            enable = hasActiveUser,
        )
        actionList += StatusInteraction.Forward(
            forwardCount = entity.reblogsCount,
            forwarded = entity.reblogged ?: false,
            enable = hasActiveUser,
        )
        actionList += StatusInteraction.Comment(
            commentCount = entity.repliesCount,
            enable = hasActiveUser,
        )
        actionList += StatusInteraction.Bookmark(
            bookmarkCount = null,
            bookmarked = entity.bookmarked ?: false,
            enable = hasActiveUser,
        )
        if (isSelfStatus) {
            actionList.add(StatusInteraction.Delete(enable = true))
            val pinned = entity.pinned == true
            actionList.add(StatusInteraction.Pin(pinned = pinned, enable = true))
        }
        return actionList
    }
}
