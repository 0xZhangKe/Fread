package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.serialization.json.Json

class IdentityRoleConverter {

    @TypeConverter
    fun fromString(string: String): IdentityRole {
        return Json.decodeFromString(IdentityRole.serializer(), string)
    }

    @TypeConverter
    fun toString(locator: PlatformLocator): String {
        return Json.encodeToString(IdentityRole.serializer(), role)
    }
}
