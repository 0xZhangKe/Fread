package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount

class ActivityPubLoggedAccountConverter {

    @TypeConverter
    fun toJsonString(blogAuthor: ActivityPubLoggedAccount): String {
        return globalJson.encodeToString(blogAuthor)
    }

    @TypeConverter
    fun toEntity(jsonString: String): ActivityPubLoggedAccount {
        return globalJson.decodeFromString(jsonString)
    }
}
