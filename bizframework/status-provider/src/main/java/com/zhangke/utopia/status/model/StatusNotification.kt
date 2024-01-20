package com.zhangke.utopia.status.model

import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.status.model.Status
import java.util.Date

data class StatusNotification (
    val id: String,
    val type: StatusNotificationType,
    val createdAt: Date,
    val account: BlogAuthor,
    val status: Status?,
    val relationshipSeveranceEvent: RelationshipSeveranceEvent,
)
