package com.zhangke.fread.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaMeta
import com.zhangke.fread.status.blog.BlogMediaType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class BlogMediaConverterHelper {

    fun fromJsonObject(jsonObject: JsonObject?): BlogMedia? {
        if (jsonObject == null) return null
        return BlogMedia(
            id = jsonObject.getString("id"),
            url = jsonObject.getString("url"),
            type = jsonObject.getString("type").let(BlogMediaType::valueOf),
            previewUrl = jsonObject.getStringOrNull("previewUrl"),
            remoteUrl = jsonObject.getStringOrNull("remoteUrl"),
            description = jsonObject.getStringOrNull("description"),
            blurhash = jsonObject.getStringOrNull("blurhash"),
            meta = jsonObject.get("meta")?.jsonObject?.let(::convertJsonObjectToMeta),
        )
    }

    fun toJsonObject(media: BlogMedia?): JsonObject? {
        if (media == null) return null
        val map = buildMap {
            put("id", JsonPrimitive(media.id))
            put("url", JsonPrimitive(media.url))
            put("type", JsonPrimitive(media.type.name))
            if (media.previewUrl != null) put("previewUrl", JsonPrimitive(media.previewUrl))
            if (media.remoteUrl != null) put("remoteUrl", JsonPrimitive(media.remoteUrl))
            if (media.description != null) put("description", JsonPrimitive(media.description))
            if (media.blurhash != null) put("blurhash", JsonPrimitive(media.blurhash))
            media.meta
                ?.let { convertMetaToJsonObject(it) }
                ?.let { put("meta", it) }
        }
        return JsonObject(map)
    }

    private fun convertJsonObjectToMeta(jsonObject: JsonObject): BlogMediaMeta {
        return when (jsonObject.getStringOrNull("type")?.let(::toType)) {
            BlogMediaType.IMAGE -> {
                globalJson.decodeFromString<BlogMediaMeta.ImageMeta>(
                    jsonObject.getStringOrNull("data").orEmpty()
                )
            }

            BlogMediaType.GIFV -> {
                globalJson.decodeFromString<BlogMediaMeta.GifvMeta>(
                    jsonObject.getStringOrNull("data").orEmpty()
                )
            }

            BlogMediaType.VIDEO -> {
                globalJson.decodeFromString<BlogMediaMeta.VideoMeta>(
                    jsonObject.getStringOrNull("data").orEmpty()
                )
            }

            BlogMediaType.AUDIO -> {
                globalJson.decodeFromString<BlogMediaMeta.AudioMeta>(
                    jsonObject.getStringOrNull("data").orEmpty()
                )
            }

            else -> throw IllegalArgumentException("Unknown type!")
        }
    }

    @TypeConverter
    private fun convertMetaToJsonObject(meta: BlogMediaMeta): JsonObject {
        val type = when (meta) {
            is BlogMediaMeta.ImageMeta -> BlogMediaType.IMAGE
            is BlogMediaMeta.GifvMeta -> BlogMediaType.GIFV
            is BlogMediaMeta.VideoMeta -> BlogMediaType.VIDEO
            is BlogMediaMeta.AudioMeta -> BlogMediaType.AUDIO
        }
        val map = buildMap {
            put("type", JsonPrimitive(type.name))
            put("data", JsonPrimitive(globalJson.encodeToString(meta)))
        }
        return JsonObject(map)
    }

    private fun toType(typeName: String): BlogMediaType {
        return BlogMediaType.valueOf(typeName)
    }

    private fun JsonObject.getString(key: String): String {
        return get(key)?.jsonPrimitive?.contentOrNull.orEmpty()
    }

    private fun JsonObject.getStringOrNull(key: String): String? {
        return get(key)?.jsonPrimitive?.contentOrNull
    }
}