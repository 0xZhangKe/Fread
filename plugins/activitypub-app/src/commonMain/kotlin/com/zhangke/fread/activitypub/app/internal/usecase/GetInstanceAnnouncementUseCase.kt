package com.zhangke.fread.activitypub.app.internal.usecase

import com.zhangke.activitypub.entities.ActivityPubAnnouncementEntity
import com.zhangke.framework.date.DateParser
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.utils.getCurrentTimeMillis
import com.zhangke.fread.status.model.PlatformLocator

class GetInstanceAnnouncementUseCase (
    private val clientManager: ActivityPubClientManager,
) {

    suspend operator fun invoke(
        baseUrl: FormalBaseUrl,
        justActive: Boolean = true,
    ): Result<List<ActivityPubAnnouncementEntity>> {
        val client = clientManager.getClient(PlatformLocator(baseUrl = baseUrl))
        return client.instanceRepo.getAnnouncement()
            .map {
                if (justActive) {
                    it.filter { item -> item.isActive() }
                } else {
                    it
                }
            }
    }

    private fun ActivityPubAnnouncementEntity.isActive(): Boolean {
        val startDateTime =
            startsAt?.let { DateParser.parseOrCurrent(it) }?.instant?.toEpochMilliseconds()
        val endDateTime =
            endsAt?.let { DateParser.parseOrCurrent(it) }?.instant?.toEpochMilliseconds()
        if (startDateTime == null) return true
        val currentDateTime = getCurrentTimeMillis()
        if (currentDateTime < startDateTime) return false
        if (endDateTime == null && allDay) return true
        if (endDateTime == null) return true
        return currentDateTime < endDateTime
    }
}