package com.zhangke.fread.bluesky.internal.db.converter

import androidx.room.TypeConverter
import app.bsky.feed.GeneratorView
import com.zhangke.framework.architect.json.fromJson
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import kotlinx.serialization.encodeToString

class GeneratorViewConverter {

    @TypeConverter
    fun fromGeneratorView(generator: GeneratorView): String {
        return bskyJson.encodeToString(generator)
    }

    @TypeConverter
    fun toGeneratorView(text: String): GeneratorView {
        return bskyJson.fromJson(text)
    }
}
