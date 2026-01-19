package com.zhangke.fread.activitypub.app.internal.repo.platform

import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.status.model.createActivityPubProtocol
import com.zhangke.fread.activitypub.app.internal.utils.MastodonHelper
import com.zhangke.fread.status.platform.PlatformSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

class BlogPlatformResourceLoader (
    private val mastodonHelper: MastodonHelper,
) {

    suspend fun loadLocalPlatforms(): List<PlatformSnapshot> = withContext(Dispatchers.IO) {
        val json = mastodonHelper.getLocalMastodonJson()
        if (json.isNullOrEmpty()) return@withContext emptyList()
        return@withContext globalJson.decodeFromString<JsonArray?>(json)
            ?.mapNotNull { it as? JsonObject }
            ?.mapNotNull { it.toPlatformSnapshot() }
            ?.distinctBy { it.domain }
            ?: emptyList()
    }

    private fun JsonObject.toPlatformSnapshot(): PlatformSnapshot? {
        val domain = getAsString("domain") ?: return null
        return PlatformSnapshot(
            domain = domain.lowercase(),
            description = getAsString("description").orEmpty(),
            thumbnail = getAsString("proxied_thumbnail").orEmpty(),
            protocol = createActivityPubProtocol(),
        )
    }

    private fun JsonObject.getAsString(key: String): String? {
        val element = get(key)
        if (element is JsonPrimitive) {
            return element.contentOrNull
        }
        return null
    }

    private fun JsonObject.getAsInt(key: String): Int? {
        val element = get(key)
        if (element is JsonPrimitive) {
            return element.intOrNull
        }
        return null
    }
}