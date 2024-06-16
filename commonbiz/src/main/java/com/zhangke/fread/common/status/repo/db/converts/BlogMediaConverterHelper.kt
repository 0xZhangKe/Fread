package com.zhangke.fread.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.google.gson.JsonObject
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.fread.status.blog.BlogMedia
import com.zhangke.fread.status.blog.BlogMediaMeta
import com.zhangke.fread.status.blog.BlogMediaType

class BlogMediaConverterHelper {

    @TypeConverter
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
            meta = jsonObject.get("meta")?.asJsonObject?.let(::convertJsonObjectToMeta),
        )
    }

    @TypeConverter
    fun toJsonObject(media: BlogMedia?): JsonObject? {
        if (media == null) return null
        val jsonObject = JsonObject()
        jsonObject.addProperty("id", media.id)
        jsonObject.addProperty("url", media.url)
        jsonObject.addProperty("type", media.type.name)
        jsonObject.addStringIfNotNull("previewUrl", media.previewUrl)
        jsonObject.addStringIfNotNull("remoteUrl", media.remoteUrl)
        jsonObject.addStringIfNotNull("description", media.description)
        jsonObject.addStringIfNotNull("blurhash", media.blurhash)
        media.meta
            ?.let { convertMetaToJsonObject(it) }
            ?.let { jsonObject.add("meta", it) }
        return jsonObject
    }

    private fun convertJsonObjectToMeta(jsonObject: JsonObject): BlogMediaMeta {
        return when (jsonObject.get("type").asString.let(::toType)) {
            BlogMediaType.IMAGE -> {
                globalGson.fromJson(
                    jsonObject.get("data").asJsonObject,
                    BlogMediaMeta.ImageMeta::class.java
                )
            }

            BlogMediaType.GIFV -> {
                globalGson.fromJson(
                    jsonObject.get("data").asJsonObject,
                    BlogMediaMeta.GifvMeta::class.java
                )
            }

            BlogMediaType.VIDEO -> {
                globalGson.fromJson(
                    jsonObject.get("data").asJsonObject,
                    BlogMediaMeta.VideoMeta::class.java
                )
            }

            BlogMediaType.AUDIO -> {
                globalGson.fromJson(
                    jsonObject.get("data").asJsonObject,
                    BlogMediaMeta.AudioMeta::class.java
                )
            }

            else -> throw IllegalArgumentException("Unknown type!")
        }
    }

    @TypeConverter
    private fun convertMetaToJsonObject(meta: BlogMediaMeta): JsonObject {
        val jsonObject = JsonObject()
        val type = when (meta) {
            is BlogMediaMeta.ImageMeta -> BlogMediaType.IMAGE
            is BlogMediaMeta.GifvMeta -> BlogMediaType.GIFV
            is BlogMediaMeta.VideoMeta -> BlogMediaType.VIDEO
            is BlogMediaMeta.AudioMeta -> BlogMediaType.AUDIO
        }
        jsonObject.addProperty("type", type.name)
        jsonObject.add("data", globalGson.toJsonTree(meta))
        return jsonObject
    }

    private fun toType(typeName: String): BlogMediaType {
        return BlogMediaType.valueOf(typeName)
    }

    private fun JsonObject.getString(key: String): String {
        return get(key).asString
    }

    private fun JsonObject.getStringOrNull(key: String): String? {
        return get(key)?.asString
    }

    private fun JsonObject.addStringIfNotNull(key: String, value: String?) {
        if (value == null) return
        addProperty(key, value)
    }
}