package com.zhangke.utopia.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.status.model.ContentConfig
import kotlinx.serialization.json.Json

class ContentTabConverter {

    @TypeConverter
    fun toJsonString(tabList: List<ContentConfig.ActivityPubContent.TabConfig>): String {
        val stringList = tabList.map {
            Json.encodeToString(ContentConfig.ActivityPubContent.TabConfig.serializer(), it)
        }
        return globalGson.toJson(stringList)
    }

    @TypeConverter
    fun toTabList(jsonText: String): List<ContentConfig.ActivityPubContent.TabConfig> {
        return globalGson.fromJson(jsonText, Array<String>::class.java)
            .map {
                Json.decodeFromString<ContentConfig.ActivityPubContent.TabConfig>(
                    deserializer = ContentConfig.ActivityPubContent.TabConfig.serializer(),
                    string = it,
                )
            }
    }
}
