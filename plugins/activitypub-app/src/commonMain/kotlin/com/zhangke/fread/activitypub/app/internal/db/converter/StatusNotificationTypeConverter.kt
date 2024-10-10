package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.fread.activitypub.app.internal.model.StatusNotificationType

class StatusNotificationTypeConverter {

    @TypeConverter
    fun convertToString(type: StatusNotificationType): String {
        return type.name
    }

    @TypeConverter
    fun convertToType(name: String): StatusNotificationType {
        return StatusNotificationType.valueOf(name)
    }
}
