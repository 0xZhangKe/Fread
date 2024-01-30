package com.zhangke.utopia.activitypub.app.internal.screen.user

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubRelationshipEntity

data class UserDetailUiState (
    val account: ActivityPubAccountEntity?,
    val relationship: ActivityPubRelationshipEntity?,
)
