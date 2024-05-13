package com.zhangke.utopia.activitypub.app.internal.repo.platform

import android.content.Context
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypub.app.getActivityPubProtocol
import com.zhangke.utopia.status.platform.PlatformSnapshot
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject

class BlogPlatformResourceLoader @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    suspend fun loadLocalPlatforms(): List<PlatformSnapshot> = withContext(Dispatchers.IO) {
        val json = getLocalMastodonJson()
        if (json.isNullOrEmpty()) return@withContext emptyList()
        return@withContext globalGson.fromJson(json, JsonArray::class.java)
            ?.mapNotNull { if (it.isJsonObject) it.asJsonObject else null }
            ?.mapNotNull { it.toPlatformSnapshot() }
            ?: emptyList()
    }

    private fun JsonObject.toPlatformSnapshot(): PlatformSnapshot? {
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
            protocol = getActivityPubProtocol(context),
        )
    }

    private fun JsonObject.getAsString(key: String): String? {
        val element = get(key)
        if (element is JsonPrimitive) {
            return element.asString
        }
        return null
    }

    private fun JsonObject.getAsInt(key: String): Int? {
        val element = get(key)
        if (element is JsonPrimitive && element.isNumber) {
            return element.asInt
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
