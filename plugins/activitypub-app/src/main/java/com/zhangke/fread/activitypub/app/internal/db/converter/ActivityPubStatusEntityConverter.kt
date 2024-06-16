package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.architect.json.globalGson

class ActivityPubStatusEntityConverter {

    @TypeConverter
    fun fromEntity(entity: ActivityPubStatusEntity): String {
        return globalGson.toJson(entity)
    }

    @TypeConverter
    fun toEntity(text: String): ActivityPubStatusEntity {
        return globalGson.fromJson(text, ActivityPubStatusEntity::class.java)
    }
}