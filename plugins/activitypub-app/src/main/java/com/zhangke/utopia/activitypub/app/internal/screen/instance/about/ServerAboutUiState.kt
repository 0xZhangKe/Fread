package com.zhangke.utopia.activitypub.app.internal.screen.instance.about

import com.zhangke.activitypub.entities.ActivityPubAnnouncementEntity
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstanceRule

internal data class ServerAboutUiState(
    val announcement: List<ActivityPubAnnouncementEntity>,
    val rules: List<ActivityPubInstanceRule>,
)
