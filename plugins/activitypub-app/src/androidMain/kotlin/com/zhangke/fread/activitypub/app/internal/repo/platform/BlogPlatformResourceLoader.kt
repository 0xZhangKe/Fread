package com.zhangke.fread.activitypub.app.internal.repo.platform

import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.utils.appContext
import com.zhangke.fread.activitypub.app.createActivityPubProtocol
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.status.platform.PlatformSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import me.tatarka.inject.annotations.Inject
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

class BlogPlatformResourceLoader @Inject constructor(
    private val context: ApplicationContext,
) {

    suspend fun loadLocalPlatforms(): List<PlatformSnapshot> = withContext(Dispatchers.IO) {
        val json = getLocalMastodonJson()
        if (json.isNullOrEmpty()) return@withContext emptyList()
        return@withContext globalJson.decodeFromString<JsonArray?>(json)
            ?.mapNotNull { it as? JsonObject }
            ?.mapNotNull { it.toPlatformSnapshot() }
            ?: emptyList()
    }

    private suspend fun JsonObject.toPlatformSnapshot(): PlatformSnapshot? {
        val domain = getAsString("domain") ?: return null
        return PlatformSnapshot(
            domain = domain,
            description = getAsString("description").orEmpty(),
            version = getAsString("version").orEmpty(),
            language = getAsString("language").orEmpty(),
            thumbnail = getAsString("proxied_thumbnail").orEmpty(),
            totalUsers = getAsInt("total_users") ?: 0,
            lastWeekUsers = getAsInt("last_week_users") ?: 0,
            category = getAsString("category").orEmpty(),
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

    private fun getLocalMastodonJson(): String? {
        try {
            val localJsonFile = getLocalCacheFile()
            if (localJsonFile.exists()) {
                localJsonFile.readText()
                    .takeIf { it.isNotBlank() }
                    ?.let { return it }
                localJsonFile.delete()
            }
            localJsonFile.createNewFile()
            context.assets
                .open("mastodon-servers.zip")
                .use { assetInput ->
                    ZipInputStream(assetInput).use { input ->
                        var zipEntry = input.nextEntry
                        while (zipEntry != null) {
                            if (zipEntry.isDirectory || !zipEntry.name.endsWith("json")) {
                                zipEntry = input.nextEntry
                                continue
                            }
                            FileOutputStream(localJsonFile).use { fileOut ->
                                BufferedOutputStream(fileOut).use { out ->
                                    val buffer = ByteArray(1024)
                                    var count: Int
                                    while (input.read(buffer).also { count = it } != -1) {
                                        out.write(buffer, 0, count)
                                    }
                                }
                            }
                            break
                        }
                    }
                }
            return localJsonFile.readText()
        } catch (t: Throwable) {
            return null
        }
    }

    private fun getLocalCacheFile(): File {
        return File(appContext.cacheDir, "mastodon-servers.json")
    }
}
