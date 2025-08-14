package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.db.old.OldActivityPubLoggedAccountEntity
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class PlatformEntityTypeConverter {

    @TypeConverter
    fun fromType(platform: OldActivityPubLoggedAccountEntity.BlogPlatformEntity): String {
        return globalJson.encodeToString(platform)
    }

    @TypeConverter
    fun toType(text: String): OldActivityPubLoggedAccountEntity.BlogPlatformEntity {
        return runCatching {
            globalJson.decodeFromString<OldActivityPubLoggedAccountEntity.BlogPlatformEntity>(text)
        }.getOrNull() ?: compatibleObfuscation(text)
    }

    private fun compatibleObfuscation(text: String): OldActivityPubLoggedAccountEntity.BlogPlatformEntity {
        val jsonObject = runCatching { globalJson.decodeFromString<JsonObject>(text) }.getOrNull()
            ?: JsonObject(emptyMap())
        val baseUrlJson = jsonObject["d"]?.let { it as? JsonObject }
        val baseUrl = FormalBaseUrl.build(
            host = baseUrlJson.getValueAsString("host", "mastodon.social"),
            scheme = baseUrlJson.getValueAsString("scheme", "https"),
        )
        val defaultUri =
            "freadapp://activitypub.com/platform?serverBaseUrl=https%3A%2F%2Fmastodon.social"
        return OldActivityPubLoggedAccountEntity.BlogPlatformEntity(
            uri = jsonObject.getValueAsString("a", defaultUri),
            name = jsonObject.getValueAsString("b", "mastodon.social"),
            description = jsonObject.getValueAsString("c", ""),
            baseUrl = baseUrl,
            thumbnail = jsonObject.getValueAsString("e", ""),
        )
    }

    private fun JsonObject?.getValueAsString(key: String, default: String): String {
        this ?: return default
        val v = this[key] ?: return default
        if (v is JsonPrimitive) {
            return v.content
        }
        return v.toString()
    }
}
