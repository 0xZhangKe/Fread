package com.zhangke.utopia.activitypub.app.internal.screen.user

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubRelationshipEntity
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights

data class UserDetailUiState (
    val userInsight: UserUriInsights?,
    val account: ActivityPubAccountEntity?,
    val relationship: ActivityPubRelationshipEntity?,
)
