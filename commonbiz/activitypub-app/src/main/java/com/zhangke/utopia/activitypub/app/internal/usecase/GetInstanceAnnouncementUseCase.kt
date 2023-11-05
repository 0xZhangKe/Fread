package com.zhangke.utopia.activitypub.app.internal.usecase

import com.zhangke.activitypub.entry.ActivityPubAnnouncementEntity
import com.zhangke.utopia.activitypub.app.internal.client.ObtainActivityPubClientUseCase
import javax.inject.Inject

class GetInstanceAnnouncementUseCase @Inject constructor(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
) {

    suspend operator fun invoke(
        host: String,
        justActive: Boolean = true,
    ): Result<List<ActivityPubAnnouncementEntity>> {
        val client = obtainActivityPubClientUseCase(host)
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
