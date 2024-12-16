package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.status.model.ContentConfig
import kotlinx.serialization.encodeToString

class ContentTabConverter {

    @TypeConverter
    fun toJsonString(tabList: List<ContentConfig.ActivityPubContent.ContentTab>): String {
        if (tabList.isEmpty()) return ""
        val stringList = tabList.map {
            globalJson.encodeToString(ContentConfig.ActivityPubContent.ContentTab.serializer(), it)
        }
        return globalJson.encodeToString(stringList)
    }

    @TypeConverter
    fun toTabList(jsonText: String): List<ContentConfig.ActivityPubContent.ContentTab> {
        if (jsonText.isEmpty()) return emptyList()
        return globalJson.decodeFromString<List<String>>(jsonText).map {
            globalJson.decodeFromString(
                deserializer = ContentConfig.ActivityPubContent.ContentTab.serializer(),
                string = it,
            )
        }
    }
}
