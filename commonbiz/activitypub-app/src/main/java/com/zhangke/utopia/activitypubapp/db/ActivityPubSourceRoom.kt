package com.zhangke.utopia.activitypubapp.db

import androidx.room.Entity
import com.google.gson.JsonObject

private const val TABLE_NAME = "sources"

@Entity(tableName = TABLE_NAME)
data class ActivityPubSourceEntry(
    val uri: String,
    val protocol: String,
    val sourceName: String,
    val sourceDescription: String?,
    val avatar: String?,
)