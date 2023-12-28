package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.utopia.status.status.model.StatusInteraction
import javax.inject.Inject

class GetStatusInteractionUseCase @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val loggedAccountAdapter: ActivityPubLoggedAccountAdapter,
) {

    suspend operator fun invoke(
        entity: ActivityPubStatusEntity,
    ): List<StatusInteraction> {
        val activeAccount = accountManager.getActiveAccount()
        val statusAuthorWebFinger = loggedAccountAdapter.accountToWebFinger(entity.account)
        val isSelfStatus = activeAccount?.webFinger == statusAuthorWebFinger
        val hasActiveUser = activeAccount != null
        val actionList = mutableListOf<StatusInteraction>()
        actionList += StatusInteraction.Like(
            likeCount = entity.favouritesCount,
            liked = entity.favourited ?: false,
            enable = hasActiveUser,
        )
        actionList += StatusInteraction.Forward(
            forwardCount = entity.reblogsCount,
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
        }
        return actionList
    }
}
