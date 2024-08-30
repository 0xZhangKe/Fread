package com.zhangke.fread.common.config

import android.content.Context
import com.zhangke.framework.utils.appContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

object FreadConfigManager {

    private const val LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO = "auto_play_inline_video"
    private const val LOCAL_KEY_STATUS_CONTENT_SIZE = "fread_status_content_size"

    private val _statusContentSizeFlow = MutableStateFlow(StatusContentSize.default())
    val statusContentSizeFlow get(): StateFlow<StatusContentSize> = _statusContentSizeFlow

    var autoPlayInlineVideo: Boolean = false
        private set

    suspend fun initConfig(context: Context) {
        val contentSize = withContext(Dispatchers.IO) {
            val sizeString = LocalConfigManager.getString(context, LOCAL_KEY_STATUS_CONTENT_SIZE)
            val size = sizeString?.toContentSize()
            if (size == null) {
                LocalConfigManager.putString(
                    context,
                    LOCAL_KEY_STATUS_CONTENT_SIZE,
                    StatusContentSize.default().name
                )
                StatusContentSize.default()
            } else {
                size
            }
        }
        _statusContentSizeFlow.value = contentSize
        autoPlayInlineVideo = withContext(Dispatchers.IO) {
            LocalConfigManager.getBoolean(context, LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO) ?: false
        }
    }

    suspend fun getStatusContentSize(context: Context): StatusContentSize {
        return withContext(Dispatchers.IO) {
            LocalConfigManager.getString(context, LOCAL_KEY_STATUS_CONTENT_SIZE)
                ?.toContentSize()
                ?: StatusContentSize.default()
        }
    }

    private fun String.toContentSize(): StatusContentSize? {
        return runCatching { StatusContentSize.valueOf(this) }.getOrNull()
    }

    suspend fun updateAutoPlayInlineVideo(value: Boolean) {
        autoPlayInlineVideo = value
        withContext(Dispatchers.IO) {
            LocalConfigManager.putBoolean(appContext, LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO, value)
        }
    }

    suspend fun updateStatusContentSize(contentSize: StatusContentSize) {
        _statusContentSizeFlow.value = contentSize
        withContext(Dispatchers.IO) {
            LocalConfigManager.putString(
                appContext,
                LOCAL_KEY_STATUS_CONTENT_SIZE,
                contentSize.name,
            )
        }
    }
}
