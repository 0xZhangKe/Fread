package com.zhangke.fread.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.fread.status.model.ContentConfig
import kotlinx.serialization.json.Json

class ContentTabConverter {

    @TypeConverter
    fun toJsonString(tabList: List<ContentConfig.ActivityPubContent.ContentTab>): String {
        val stringList = tabList?.map {
            Json.encodeToString(ContentConfig.ActivityPubContent.ContentTab.serializer(), it)
        }
        return globalGson.toJson(stringList)
    }

    @TypeConverter
    fun toTabList(jsonText: String): List<ContentConfig.ActivityPubContent.ContentTab> {
        return globalGson.fromJson(jsonText, Array<String>::class.java)
            .map {
                Json.decodeFromString(
                    deserializer = ContentConfig.ActivityPubContent.ContentTab.serializer(),
                    string = it,
                )
            }
    }
}
