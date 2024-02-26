package com.zhangke.utopia.rss.internal.utils

import android.content.Context
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.network.HttpScheme
import com.zhangke.framework.security.Md5
import com.zhangke.framework.utils.BitmapUtils
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.rss.internal.model.RssSource
import java.io.File

object AvatarUtils {

    private const val AVATAR_WIDTH = 100
    private const val AVATAR_HEIGHT = 100
    private const val AVATAR_DIR = "avatars"

    fun makeSourceAvatar(
        source: RssSource,
    ): String? {
        val text = source.displayName.ifNullOrEmpty { source.title }.take(2)
        val bitmap = BitmapUtils.buildBitmapWithText(
            width = AVATAR_WIDTH,
            height = AVATAR_HEIGHT,
            text = text,
            backgroundColor = 0xFFFF00FF.toInt(),
        )
        val avatarFile = getAvatarFile(source.url)
        return try {
            if (!avatarFile.exists()) {
                avatarFile.parentFile?.mkdirs()
                avatarFile.createNewFile()
            }
            BitmapUtils.saveToFile(bitmap, avatarFile)
            avatarFile.absolutePath
        } catch (e: Throwable) {
            null
        }
    }

    fun getAvatarFile(sourceUrl: String): File {
        val fileName = "${Md5.md5(sourceUrl)}.png"
        return File(getAvatarDirPath(), fileName)
    }

    fun isLocalAvatar(avatar: String?): Boolean {
        avatar ?: return false
        val fixedAvatar = avatar.lowercase()
        return fixedAvatar.isNotEmpty() &&
            !fixedAvatar.startsWith(HttpScheme.HTTP) &&
            !fixedAvatar.startsWith(HttpScheme.HTTPS)
    }

    fun isRemoteAvatar(avatar: String?): Boolean {
        avatar ?: return false
        val fixedAvatar = avatar.lowercase()
        return fixedAvatar.startsWith(HttpScheme.HTTP) || fixedAvatar.startsWith(HttpScheme.HTTPS)
    }

    private fun getAvatarDirPath(): String {
        return appContext.getDir(AVATAR_DIR, Context.MODE_PRIVATE).absolutePath
    }
}
