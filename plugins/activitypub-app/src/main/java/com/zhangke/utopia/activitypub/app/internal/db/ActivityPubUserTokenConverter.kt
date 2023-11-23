package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.TypeConverter
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.architect.json.globalGson

class ActivityPubUserTokenConverter {

    @TypeConverter
    fun fromType(token: ActivityPubTokenEntity): String {
        return globalGson.toJson(token)
    }

    @TypeConverter
    fun toType(text: String): ActivityPubTokenEntity {
        return globalGson.fromJson(text, ActivityPubTokenEntity::class.java)
    }
}
