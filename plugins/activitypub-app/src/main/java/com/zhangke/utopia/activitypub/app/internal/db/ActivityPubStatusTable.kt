package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType

private const val TABLE_NAME = "activity_pub_status"

@Entity(tableName = TABLE_NAME)
data class ActivityPubStatusEntity(
    @PrimaryKey val id: String,
    val type: ActivityPubStatusSourceType,
    val baseUrl: FormalBaseUrl,
)
