package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubNotificationsEntity
import com.zhangke.activitypub.entities.ActivityPubRelationshipSeveranceEventEntity
import com.zhangke.fread.activitypub.app.internal.model.RelationshipSeveranceEvent
import com.zhangke.fread.activitypub.app.internal.model.StatusNotification
import com.zhangke.fread.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.fread.activitypub.app.internal.usecase.FormatActivityPubDatetimeToDateUseCase
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class ActivityPubNotificationEntityAdapter @Inject constructor(
    private val formatDatetimeToDate: FormatActivityPubDatetimeToDateUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
) {

    suspend fun toNotification(
        entity: ActivityPubNotificationsEntity,
        platform: BlogPlatform,
    ) = StatusNotification(
        id = entity.id,
        type = entity.type.convertToNotificationType(),
        createdAt = formatDatetimeToDate(entity.createdAt),
        account = entity.account,
        status = entity.status?.let { statusAdapter.toStatus(it, platform) },
        relationshipSeveranceEvent = entity.relationshipSeveranceEvent?.toRelationshipSeveranceEvent(),
    )

    private fun ActivityPubRelationshipSeveranceEventEntity.toRelationshipSeveranceEvent(): RelationshipSeveranceEvent {
        return RelationshipSeveranceEvent(
            id = this.id,
            type = this.type.convertToEventType(),
            createdAt = formatDatetimeToDate(this.createdAt),
            purged = this.purged,
            targetName = this.targetName,
            relationshipsCount = this.relationshipsCount,
        )
    }

    private fun String.convertToNotificationType() = when (this) {
        ActivityPubNotificationsEntity.Type.follow -> StatusNotificationType.FOLLOW
        ActivityPubNotificationsEntity.Type.followRequest -> StatusNotificationType.FOLLOW_REQUEST
        ActivityPubNotificationsEntity.Type.mention -> StatusNotificationType.MENTION
        ActivityPubNotificationsEntity.Type.reblog -> StatusNotificationType.REBLOG
        ActivityPubNotificationsEntity.Type.favourite -> StatusNotificationType.FAVOURITE
        ActivityPubNotificationsEntity.Type.poll -> StatusNotificationType.POLL
        ActivityPubNotificationsEntity.Type.status -> StatusNotificationType.STATUS
        ActivityPubNotificationsEntity.Type.update -> StatusNotificationType.UPDATE
        ActivityPubNotificationsEntity.Type.severedRelationships -> StatusNotificationType.SEVERED_RELATIONSHIPS
        else -> StatusNotificationType.UNKNOWN
    }

    private fun String.convertToEventType() = when (this) {
        ActivityPubRelationshipSeveranceEventEntity.Type.domainBlock -> RelationshipSeveranceEvent.Type.DOMAIN_BLOCK
        ActivityPubRelationshipSeveranceEventEntity.Type.userDomainBlock -> RelationshipSeveranceEvent.Type.USER_DOMAIN_BLOCK
        ActivityPubRelationshipSeveranceEventEntity.Type.accountSuspension -> RelationshipSeveranceEvent.Type.ACCOUNT_SUSPENSION
        else -> throw IllegalArgumentException("Unknown event type: $this")
    }
}