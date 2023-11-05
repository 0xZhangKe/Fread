package com.zhangke.utopia.activitypub.app.internal.screen.server.about

import com.zhangke.activitypub.entry.ActivityPubAnnouncementEntity
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstanceRule

internal data class ServerAboutUiState(
    val announcement: List<ActivityPubAnnouncementEntity>,
    val rules: List<ActivityPubInstanceRule>,
)
