package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.fromJson
import com.zhangke.fread.common.commonComponentProvider
import com.zhangke.fread.status.model.FreadContent
import kotlinx.serialization.encodeToString

class FreadContentConverter {

    private val json = commonComponentProvider.component.statusProvider.contentManager.contentJson

    @TypeConverter
    fun fromJsonText(text: String): FreadContent {
        return json.fromJson(text)
    }

    @TypeConverter
    fun toJsonText(content: FreadContent): String {
        return json.encodeToString(content)
    }
}
