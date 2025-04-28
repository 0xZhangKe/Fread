package com.zhangke.fread.activitypub.app.internal.utils

import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.utils.StorageHelper
import me.tatarka.inject.annotations.Inject
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM
import okio.openZip
import org.jetbrains.compose.resources.ExperimentalResourceApi

@ApplicationScope
class MastodonHelper @Inject constructor(
    private val storageHelper: StorageHelper,
) {

    private val fileSystem = FileSystem.SYSTEM

    @OptIn(ExperimentalResourceApi::class)
    suspend fun getLocalMastodonJson(): String? = runCatching {
        val cacheMastodonServersZipPath = storageHelper.cacheDir.resolve("mastodon-servers.zip")

        // Res.getUri is not available, so copy to cache dir
        if (!fileSystem.exists(cacheMastodonServersZipPath)) {
            fileSystem.write(cacheMastodonServersZipPath) {
                write(Res.readBytes("files/mastodon-servers.zip"))
            }
        }

        val zip = FileSystem.SYSTEM.openZip(cacheMastodonServersZipPath)
        return zip.read("servers.json".toPath()) {
            readUtf8()
        }
    }.getOrNull()
}
