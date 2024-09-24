package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType

class ActivityPubStatusSourceTypeConverter {

    @TypeConverter
    fun fromType(type: ActivityPubStatusSourceType): String {
        return type.name
    }

    @TypeConverter
    fun toType(text: String): ActivityPubStatusSourceType {
        return ActivityPubStatusSourceType.valueOf(text)
    }
}
