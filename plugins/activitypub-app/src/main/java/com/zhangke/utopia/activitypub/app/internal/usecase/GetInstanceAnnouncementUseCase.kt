package com.zhangke.utopia.activitypub.app.internal.usecase

import com.zhangke.activitypub.entities.ActivityPubAnnouncementEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import javax.inject.Inject

class GetInstanceAnnouncementUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
) {

    suspend operator fun invoke(
        baseUrl: FormalBaseUrl,
        justActive: Boolean = true,
    ): Result<List<ActivityPubAnnouncementEntity>> {
        val client = clientManager.getClient(baseUrl)
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
        val startDateTime = startsAt?.let { formatDatetimeToDate(it) }?.time
        val endDateTime = endsAt?.let { formatDatetimeToDate(it) }?.time
        if (startDateTime == null) return true
        val currentDateTime = System.currentTimeMillis()
        if (currentDateTime < startDateTime) return false
        if (endDateTime == null && allDay) return true
        if (endDateTime == null) return true
        return currentDateTime < endDateTime
    }
}
