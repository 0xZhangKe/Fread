package com.zhangke.utopia.activitypub.app.internal.screen.user

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubRelationshipEntity
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.status.model.IdentityRole

data class UserDetailUiState (
    val role: IdentityRole,
    val userInsight: UserUriInsights?,
    val account: ActivityPubAccountEntity?,
    val relationship: ActivityPubRelationshipEntity?,
    val domainBlocked: Boolean?,
    val editable: Boolean,
)
