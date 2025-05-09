package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.status.notification.StatusNotification
import kotlinx.serialization.serializer

class StatusNotificationConverter {

    @TypeConverter
    fun convertToNotification(jsonString: String): StatusNotification {
        return globalJson.decodeFromString(serializer(), jsonString)
    }

    @TypeConverter
    fun convertToString(notification: StatusNotification): String {
        return globalJson.encodeToString(serializer(), notification)
    }
}