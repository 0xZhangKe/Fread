package com.zhangke.utopia.activitypubapp.db

import androidx.room.TypeConverter
import com.zhangke.activitypub.entry.ActivityPubToken
import com.zhangke.framework.architect.json.globalGson

class ActivityPubUserTokenConverter {

    @TypeConverter
    fun fromType(token: ActivityPubToken): String {
        return globalGson.toJson(token)
    }

    @TypeConverter
    fun toType(text: String): ActivityPubToken {
        return globalGson.fromJson(text, ActivityPubToken::class.java)
    }
}
