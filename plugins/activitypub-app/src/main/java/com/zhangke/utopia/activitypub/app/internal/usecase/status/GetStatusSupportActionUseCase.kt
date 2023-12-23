package com.zhangke.utopia.activitypub.app.internal.usecase.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.utopia.status.status.model.StatusAction
import javax.inject.Inject

class GetStatusSupportActionUseCase @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val loggedAccountAdapter: ActivityPubLoggedAccountAdapter,
) {

    suspend operator fun invoke(
        entity: ActivityPubStatusEntity,
    ): List<StatusAction> {
        val activeAccount = accountManager.getActiveAccount()
        val statusAuthorWebFinger = loggedAccountAdapter.accountToWebFinger(entity.account)
        val isSelfStatus = activeAccount?.webFinger == statusAuthorWebFinger
        val hasActiveUser = activeAccount != null
        val actionList = mutableListOf<StatusAction>()
        actionList += StatusAction.Like(
            likeCount = entity.favouritesCount,
            liked = entity.favourited ?: false,
            enable = hasActiveUser,
        )
        actionList += StatusAction.Forward(
            forwardCount = entity.reblogsCount,
            enable = hasActiveUser,
        )
        actionList += StatusAction.Comment(
            commentCount = entity.repliesCount,
            enable = hasActiveUser,
        )
        actionList += StatusAction.Bookmark(
            bookmarkCount = null,
            bookmarked = entity.bookmarked ?: false,
            enable = hasActiveUser,
        )
        if (isSelfStatus) {
            actionList.add(StatusAction.Delete(enable = true))
        }
        return actionList
    }
}
