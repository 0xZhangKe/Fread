package com.zhangke.utopia.activitypub.app.internal.model

import java.util.Date

data class RelationshipSeveranceEvent(
    val id: String,
    val type: Type,
    val purged: Boolean,
    val targetName: String,
    val relationshipsCount: Int?,
    val createdAt: Date,
) {

    enum class Type {

        DOMAIN_BLOCK,
        USER_DOMAIN_BLOCK,
        ACCOUNT_SUSPENSION,
    }
}
