package com.zhangke.utopia.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.architect.json.globalGson

class ActivityPubInstanceEntityConverter {

    @TypeConverter
    fun fromEntity(entity: ActivityPubInstanceEntity): String {
        return globalGson.toJson(entity)
    }

    @TypeConverter
    fun toEntity(text: String): ActivityPubInstanceEntity {
        return globalGson.fromJson(text, ActivityPubInstanceEntity::class.java)
    }
}
