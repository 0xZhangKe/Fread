package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.activitypub.app.internal.model.RelationshipSeveranceEvent
import kotlinx.serialization.encodeToString

class RelationshipSeveranceEventConverter {

    @TypeConverter
    fun fromType(platform: RelationshipSeveranceEvent?): String? {
        return platform?.let { globalJson.encodeToString(it) }
    }

    @TypeConverter
    fun toType(text: String?): RelationshipSeveranceEvent? {
        if (text.isNullOrEmpty()) return null
        return globalJson.decodeFromString(text)
    }
}
