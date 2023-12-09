package com.zhangke.utopia.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.utopia.status.status.model.StatusType

class StatusTypeConverter {

    @TypeConverter
    fun fromString(value: String): StatusType {
        return StatusType.valueOf(value)
    }

    @TypeConverter
    fun toString(uri: StatusType): String {
        return uri.toString()
    }
}