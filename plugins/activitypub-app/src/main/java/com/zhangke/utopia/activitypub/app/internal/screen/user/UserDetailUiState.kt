package com.zhangke.utopia.activitypub.app.internal.screen.user

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubRelationshipEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.status.model.Emoji
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.uri.FormalUri

data class UserDetailUiState (
    val role: IdentityRole,
    val userInsight: UserUriInsights?,
    val accountUiState: UserDetailAccountUiState?,
    val relationship: ActivityPubRelationshipEntity?,
    val domainBlocked: Boolean?,
    val editable: Boolean,
)

data class UserDetailAccountUiState(
    val account: ActivityPubAccountEntity,
    val emojis: List<Emoji>,
)
