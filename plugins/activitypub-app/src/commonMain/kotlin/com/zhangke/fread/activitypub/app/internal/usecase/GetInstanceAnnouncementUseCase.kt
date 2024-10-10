package com.zhangke.fread.activitypub.app.internal.usecase

import com.zhangke.activitypub.entities.ActivityPubAnnouncementEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.ext.getCurrentTimeMillis
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class GetInstanceAnnouncementUseCase @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
) {

    suspend operator fun invoke(
        baseUrl: FormalBaseUrl,
        justActive: Boolean = true,
    ): Result<List<ActivityPubAnnouncementEntity>> {
        val client = clientManager.getClient(IdentityRole(null, baseUrl))
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
        val startDateTime = startsAt?.let { formatDatetimeToDate(it) }?.toEpochMilliseconds()
        val endDateTime = endsAt?.let { formatDatetimeToDate(it) }?.toEpochMilliseconds()
        if (startDateTime == null) return true
        val currentDateTime = getCurrentTimeMillis()
        if (currentDateTime < startDateTime) return false
        if (endDateTime == null && allDay) return true
        if (endDateTime == null) return true
        return currentDateTime < endDateTime
    }
}
