package com.zhangke.utopia.activitypub.app.internal.repo.status

import com.zhangke.activitypub.entities.ActivityPubStatusEntity

data class ActivityPubRefreshStatusResult(
    val newStatus: List<ActivityPubStatusEntity>,
    val deletedStatus: List<ActivityPubStatusEntity>,
)
