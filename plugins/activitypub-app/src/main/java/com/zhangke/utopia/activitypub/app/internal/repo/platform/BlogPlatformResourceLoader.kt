package com.zhangke.utopia.activitypub.app.internal.repo.platform

import android.content.Context
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypub.app.internal.repo.platform.BlogPlatformResourceLoader.toBlogPlatform
import com.zhangke.utopia.activitypub.app.internal.uri.PlatformUriTransformer
import com.zhangke.utopia.status.platform.BlogPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class BlogPlatformResourceLoader @Inject constructor(
    private val platformUriTransformer: PlatformUriTransformer,
    @ApplicationContext private val context: Context,
) {

    suspend fun loadLocalPlatforms(): List<BlogPlatform> = withContext(Dispatchers.IO) {
        val json = getLocalMastodonJson()
        if (json.isNullOrEmpty()) return@withContext emptyList()

        return@withContext emptyList()
    }

    private fun JsonObject.toBlogPlatform(): BlogPlatform?{
        val baseUrl = getAsString("domain")?.let(FormalBaseUrl::parse) ?: return null
        val uri = platformUriTransformer.build(baseUrl)
        val name = getAsString("name")?.takeIf { it.isNotEmpty() } ?: return null
        return BlogPlatform(
            uri = uri.toString(),
            baseUrl = baseUrl,
            name = name,
            description = getAsString("description").orEmpty(),

        )
    }

    private fun JsonObject.getAsString(key: String): String? {
        val element = get(key)
        if (element is JsonPrimitive){
            return element.asString
        }
        return null
    }

    private fun getLocalMastodonJson(): String? {
        try {
            val localJsonFile = getLocalCacheFile()
            if (localJsonFile.exists()) {
                return localJsonFile.readText()
            } else {
                localJsonFile.createNewFile()
            }
            context.assets
                .open("mastodon-servers.zip")
                .use { input ->
                    FileOutputStream(localJsonFile).use { out ->
                        BufferedOutputStream(out)
                    }.use { out ->
                        val buffer = ByteArray(1024)
                        var count: Int
                        while (input.read(buffer).also { count = it } != -1) {
                            out.write(buffer, 0, count)
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
