package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.fromJson
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.status.model.FreadContent
import kotlinx.serialization.encodeToString

class FreadContentConverter {

    @TypeConverter
    fun fromJsonText(text: String): FreadContent {
        return globalJson.fromJson(text)
    }

    @TypeConverter
    fun toJsonText(content: FreadContent): String {
        return globalJson.encodeToString(content)
    }
}
