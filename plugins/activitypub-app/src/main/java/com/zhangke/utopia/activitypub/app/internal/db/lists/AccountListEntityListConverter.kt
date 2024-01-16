package com.zhangke.utopia.activitypub.app.internal.db.lists

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.framework.architect.json.globalGson

class AccountListEntityListConverter {

    @TypeConverter
    fun fromEntity(entity: List<ActivityPubListEntity>): String {
        return globalGson.toJson(entity)
    }

    @TypeConverter
    fun toEntity(text: String): List<ActivityPubListEntity> {
        return globalGson.fromJson(text, object : TypeToken<List<ActivityPubListEntity>>() {}.type)
    }
}
