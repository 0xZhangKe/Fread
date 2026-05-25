package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.fromJson
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.common.ai.model.LLMModelConfig

class LLMModelConfigsConverter {

    @TypeConverter
    fun fromJsonText(text: String): LLMModelConfig {
        return globalJson.fromJson(text)
    }

    @TypeConverter
    fun toJsonText(provider: LLMModelConfig): String {
        return globalJson.encodeToString(provider)
    }
}
