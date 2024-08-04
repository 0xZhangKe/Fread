package com.zhangke.fread.common.config

import android.content.Context
import com.zhangke.framework.utils.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext

object FreadConfigManager {

    private const val LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO = "auto_play_inline_video"
    private const val LOCAL_KEY_STATUS_CONTENT_SIZE = "status_content_size"

    private val _appFontSizeFlow = MutableSharedFlow<AppFontSize>()
    val appFontSizeFlow: SharedFlow<AppFontSize> get() = _appFontSizeFlow

    var autoPlayInlineVideo: Boolean = false
        private set

    suspend fun initConfig(context: Context) {
        autoPlayInlineVideo = withContext(Dispatchers.IO) {
            LocalConfigManager.getBoolean(context, LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO) ?: false
        }
        val contentSize = withContext(Dispatchers.IO) {
            val sizeString = LocalConfigManager.getString(context, LOCAL_KEY_STATUS_CONTENT_SIZE)
            val size = sizeString?.toAppFontSize()
            if (size == null) {
                LocalConfigManager.putString(
                    context,
                    LOCAL_KEY_STATUS_CONTENT_SIZE,
                    AppFontSize.MEDIUM.name
                )
                AppFontSize.MEDIUM
            } else {
                size
            }
        }
        _appFontSizeFlow.emit(contentSize)
    }

    suspend fun getAppFontSize(context: Context): AppFontSize {
        return withContext(Dispatchers.IO) {
            LocalConfigManager.getString(context, LOCAL_KEY_STATUS_CONTENT_SIZE)
                ?.toAppFontSize()
                ?: AppFontSize.MEDIUM
        }
    }

    private fun String.toAppFontSize(): AppFontSize? {
        return runCatching { AppFontSize.valueOf(this) }.getOrNull()
    }

    suspend fun updateAutoPlayInlineVideo(value: Boolean) {
        autoPlayInlineVideo = value
        withContext(Dispatchers.IO) {
            LocalConfigManager.putBoolean(appContext, LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO, value)
        }
    }

    suspend fun updateAppFontSize(value: AppFontSize) {
        _appFontSizeFlow.emit(value)
        withContext(Dispatchers.IO) {
            LocalConfigManager.putString(appContext, LOCAL_KEY_STATUS_CONTENT_SIZE, value.name)
        }
    }
}
