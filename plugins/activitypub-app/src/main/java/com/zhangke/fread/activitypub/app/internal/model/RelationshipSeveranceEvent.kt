package com.zhangke.fread.activitypub.app.internal.model

import com.zhangke.framework.serialize.TimestampAsInstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RelationshipSeveranceEvent(
    val id: String,
    val type: Type,
    val purged: Boolean,
    val targetName: String,
    val relationshipsCount: Int?,
    @Serializable(with = TimestampAsInstantSerializer::class) val createdAt: Instant,
) {

    enum class Type {

        DOMAIN_BLOCK,
        USER_DOMAIN_BLOCK,
        ACCOUNT_SUSPENSION,
    }
}
