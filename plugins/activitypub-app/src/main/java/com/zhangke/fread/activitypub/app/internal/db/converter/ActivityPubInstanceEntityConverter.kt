package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.architect.json.globalJson
import kotlinx.serialization.encodeToString

class ActivityPubInstanceEntityConverter {

    @TypeConverter
    fun fromEntity(entity: ActivityPubInstanceEntity): String {
        return globalJson.encodeToString(entity)
    }

    @TypeConverter
    fun toEntity(text: String): ActivityPubInstanceEntity {
        return globalJson.decodeFromString(text)
    }
}
