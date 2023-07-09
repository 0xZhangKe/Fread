package com.zhangke.utopia.activitypubapp.screen.server.about

import com.zhangke.activitypub.entry.ActivityPubAnnouncementEntity
import com.zhangke.utopia.activitypubapp.model.ActivityPubInstanceRule

internal data class ServerAboutUiState(
    val announcement: List<ActivityPubAnnouncementEntity>,
    val rules: List<ActivityPubInstanceRule>,
)
