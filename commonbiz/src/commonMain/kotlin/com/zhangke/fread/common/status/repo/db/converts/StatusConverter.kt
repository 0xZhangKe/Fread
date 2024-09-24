package com.zhangke.fread.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.fread.status.status.model.Status
import kotlinx.serialization.json.Json

class StatusConverter {

    @TypeConverter
    fun fromString(string: String?): Status? {
        string ?: return null
        return Json.decodeFromString(Status.serializer(), string)
    }

    @TypeConverter
    fun toString(status: Status?): String? {
        status ?: return null
        return Json.encodeToString(Status.serializer(), status)
    }
}
