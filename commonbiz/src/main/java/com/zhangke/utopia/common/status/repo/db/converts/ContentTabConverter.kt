package com.zhangke.utopia.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.status.model.ContentConfig

class ContentTabConverter {

    @TypeConverter
    fun toJsonString(tabList: List<ContentConfig.ActivityPubContent.ContentTab>): String {
        return globalGson.toJson(tabList)
    }

    @TypeConverter
    fun toTabList(jsonText: String): List<ContentConfig.ActivityPubContent.ContentTab> {
        return globalGson.fromJson(jsonText, List<ContentConfig.ActivityPubContent.ContentTab>::class.java).toList()
    }
}
