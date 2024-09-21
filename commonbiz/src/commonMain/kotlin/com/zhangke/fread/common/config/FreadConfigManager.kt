package com.zhangke.fread.common.config

import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class FreadConfigManager @Inject constructor(
    private val localConfigManager: LocalConfigManager,
) {
    companion object {
        private const val LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO = "auto_play_inline_video"
        private const val LOCAL_KEY_STATUS_CONTENT_SIZE = "fread_status_content_size"
    }

    private val _statusContentSizeFlow = MutableStateFlow(StatusContentSize.default())
    val statusContentSizeFlow get(): StateFlow<StatusContentSize> = _statusContentSizeFlow

    var autoPlayInlineVideo: Boolean = false
        private set

    suspend fun initConfig() = withContext(Dispatchers.IO) {
        val sizeString = localConfigManager.getString(LOCAL_KEY_STATUS_CONTENT_SIZE)
        val size = sizeString?.toContentSize()
        val contentSize = if (size == null) {
            localConfigManager.putString(
                LOCAL_KEY_STATUS_CONTENT_SIZE,
                StatusContentSize.default().name
            )
            StatusContentSize.default()
        } else {
            size
        }
        _statusContentSizeFlow.value = contentSize
        autoPlayInlineVideo =
            localConfigManager.getBoolean(LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO) ?: false
    }

    suspend fun getStatusContentSize(): StatusContentSize {
        return withContext(Dispatchers.IO) {
            localConfigManager.getString(LOCAL_KEY_STATUS_CONTENT_SIZE)
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
            localConfigManager.putBoolean(LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO, value)
        }
    }

    suspend fun updateStatusContentSize(contentSize: StatusContentSize) {
        _statusContentSizeFlow.value = contentSize
        withContext(Dispatchers.IO) {
            localConfigManager.putString(
                LOCAL_KEY_STATUS_CONTENT_SIZE,
                contentSize.name,
            )
        }
    }
}

val LocalFreadConfigManager =
    staticCompositionLocalOf<FreadConfigManager> { error("No FreadConfigManager provided") }
