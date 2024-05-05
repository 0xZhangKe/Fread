package com.zhangke.utopia.activitypub.app.internal.screen.user.about

import com.zhangke.activitypub.entities.ActivityPubField
import com.zhangke.utopia.status.model.Emoji

data class UserAboutUiState(
    val joinedDatetime: String?,
    val fieldList: List<ActivityPubField>,
    val emojis: List<Emoji>,
)
