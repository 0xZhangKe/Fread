package com.zhangke.fread.activitypub.app.internal.screen.instance.about

import com.zhangke.activitypub.entities.ActivityPubAnnouncementEntity
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubInstanceRule

data class ServerAboutUiState(
    val announcement: List<ActivityPubAnnouncementEntity>,
    val rules: List<ActivityPubInstanceEntity.Rule>,
)
