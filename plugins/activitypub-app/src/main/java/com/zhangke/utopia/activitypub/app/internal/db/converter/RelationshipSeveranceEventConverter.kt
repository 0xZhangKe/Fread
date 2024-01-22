package com.zhangke.utopia.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypub.app.internal.model.RelationshipSeveranceEvent

class RelationshipSeveranceEventConverter {

    @TypeConverter
    fun fromType(platform: RelationshipSeveranceEvent): String {
        return globalGson.toJson(platform)
    }

    @TypeConverter
    fun toType(text: String): RelationshipSeveranceEvent {
        return globalGson.fromJson(text, RelationshipSeveranceEvent::class.java)
    }
}
