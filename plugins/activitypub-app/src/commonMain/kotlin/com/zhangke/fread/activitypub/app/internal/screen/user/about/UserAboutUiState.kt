package com.zhangke.fread.activitypub.app.internal.screen.user.about

import com.zhangke.activitypub.entities.ActivityPubField
import com.zhangke.fread.status.model.Emoji

data class UserAboutUiState(
    val joinedDatetime: String?,
    val fieldList: List<ActivityPubField>,
    val emojis: List<Emoji>,
)
