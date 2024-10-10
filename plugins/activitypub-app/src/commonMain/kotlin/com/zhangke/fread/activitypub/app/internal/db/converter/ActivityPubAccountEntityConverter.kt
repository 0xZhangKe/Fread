package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.architect.json.globalJson
import kotlinx.serialization.encodeToString

class ActivityPubAccountEntityConverter {

    @TypeConverter
    fun toBlogAuthorString(blogAuthor: ActivityPubAccountEntity): String {
        return globalJson.encodeToString(blogAuthor)
    }

    @TypeConverter
    fun toBlogAuthor(jsonString: String): ActivityPubAccountEntity {
        return globalJson.decodeFromString(jsonString)
    }
}
