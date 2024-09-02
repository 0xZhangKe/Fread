package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.architect.json.globalJson
import kotlinx.serialization.encodeToString

class ActivityPubUserTokenConverter {

    @TypeConverter
    fun fromType(token: ActivityPubTokenEntity): String {
        return globalJson.encodeToString(token)
    }

    @TypeConverter
    fun toType(text: String): ActivityPubTokenEntity {
        return globalJson.decodeFromString(text)
    }
}
