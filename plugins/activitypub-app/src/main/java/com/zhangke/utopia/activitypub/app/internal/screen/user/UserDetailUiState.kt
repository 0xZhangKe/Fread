package com.zhangke.utopia.activitypub.app.internal.screen.user

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubRelationshipEntity
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.status.model.Emoji
import com.zhangke.utopia.status.model.IdentityRole

data class UserDetailUiState (
    val role: IdentityRole,
    val loading: Boolean,
    val userInsight: UserUriInsights?,
    val accountUiState: UserDetailAccountUiState?,
    val relationship: ActivityPubRelationshipEntity?,
    val domainBlocked: Boolean?,
    val isAccountOwner: Boolean,
)

data class UserDetailAccountUiState(
    val account: ActivityPubAccountEntity,
    val emojis: List<Emoji>,
)
