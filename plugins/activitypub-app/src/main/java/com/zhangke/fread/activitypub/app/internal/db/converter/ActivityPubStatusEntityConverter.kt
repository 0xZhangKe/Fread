package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.architect.json.globalJson
import kotlinx.serialization.encodeToString

class ActivityPubStatusEntityConverter {

    @TypeConverter
    fun fromEntity(entity: ActivityPubStatusEntity): String {
        return globalJson.encodeToString(entity)
    }

    @TypeConverter
    fun toEntity(text: String): ActivityPubStatusEntity {
        return globalJson.decodeFromString(text)
    }
}