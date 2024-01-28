package com.zhangke.utopia.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.architect.json.globalGson

class ActivityPubAccountEntityConverter {

    @TypeConverter
    fun toBlogAuthorString(blogAuthor: ActivityPubAccountEntity): String {
        return globalGson.toJson(blogAuthor)
    }

    @TypeConverter
    fun toBlogAuthor(jsonString: String): ActivityPubAccountEntity {
        return globalGson.fromJson(jsonString, ActivityPubAccountEntity::class.java)
    }
}
