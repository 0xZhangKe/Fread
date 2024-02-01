package com.zhangke.utopia.activitypub.app.internal.screen.user.about

import com.zhangke.activitypub.entities.ActivityPubField

data class UserAboutUiState (
    val joinedDatetime: String?,
    val fieldList: List<ActivityPubField>,
)
