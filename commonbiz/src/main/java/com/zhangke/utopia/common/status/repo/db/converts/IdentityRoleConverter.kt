package com.zhangke.utopia.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.utopia.status.model.IdentityRole
import kotlinx.serialization.json.Json

class IdentityRoleConverter {

    @TypeConverter
    fun fromString(string: String): IdentityRole {
        return Json.decodeFromString(IdentityRole.serializer(), string)
    }

    @TypeConverter
    fun toString(role: IdentityRole): String {
        return Json.encodeToString(IdentityRole.serializer(), role)
    }
}
