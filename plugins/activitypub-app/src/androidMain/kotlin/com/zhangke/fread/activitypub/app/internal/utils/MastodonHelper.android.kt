package com.zhangke.fread.activitypub.app.internal.utils

import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

@ApplicationScope
actual class MastodonHelper @Inject constructor(
    private val context: ApplicationContext,
) {
    actual fun getLocalMastodonJson(): String? {
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
        return File(context.cacheDir, "mastodon-servers.json")
    }
}