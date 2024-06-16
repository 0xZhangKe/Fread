package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.fread.activitypub.app.internal.model.RelationshipSeveranceEvent

class RelationshipSeveranceEventConverter {

    @TypeConverter
    fun fromType(platform: RelationshipSeveranceEvent?): String? {
        return globalGson.toJson(platform)
    }

    @TypeConverter
    fun toType(text: String?): RelationshipSeveranceEvent? {
        if (text.isNullOrEmpty()) return null
        return globalGson.fromJson(text, RelationshipSeveranceEvent::class.java)
    }
}
