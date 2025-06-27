package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.serialization.json.Json

class PlatformLocatorConverter {

    @TypeConverter
    fun fromString(string: String): PlatformLocator {
        return Json.decodeFromString(PlatformLocator.serializer(), string)
    }

    @TypeConverter
    fun toString(locator: PlatformLocator): String {
        return Json.encodeToString(PlatformLocator.serializer(), locator)
    }
}
