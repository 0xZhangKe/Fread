package com.zhangke.fread.common.config

import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.utils.RandomIdGenerator
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
        private const val LOCAL_KEY_STATUS_ALWAYS_SHOW_SENSITIVE =
            "fread_status_always_show_sensitive"
        private const val LOCAL_KEY_DEVICE_ID = "device_id"
        private const val LOCAL_KEY_IGNORE_UPDATE_VERSION = "ignore_update_version"
    }

    private val _statusConfigFlow = MutableStateFlow(StatusConfig.default())
    val statusConfigFlow get(): StateFlow<StatusConfig> = _statusConfigFlow

    var autoPlayInlineVideo: Boolean = false
        private set

    suspend fun initConfig() {
        _statusConfigFlow.value = readLocalStatusConfig()
        autoPlayInlineVideo =
            localConfigManager.getBoolean(LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO) ?: false
    }

    private suspend fun readLocalStatusConfig(): StatusConfig {
        val alwaysShowSensitiveContent =
            localConfigManager.getBoolean(LOCAL_KEY_STATUS_ALWAYS_SHOW_SENSITIVE) ?: false
        val contentSize = localConfigManager.getString(LOCAL_KEY_STATUS_CONTENT_SIZE)
            ?.toContentSize()
            ?: StatusContentSize.default()
        return StatusConfig(
            alwaysShowSensitiveContent = alwaysShowSensitiveContent,
            contentSize = contentSize,
        )
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
        _statusConfigFlow.value = _statusConfigFlow.value.copy(contentSize = contentSize)
        withContext(Dispatchers.IO) {
            localConfigManager.putString(
                LOCAL_KEY_STATUS_CONTENT_SIZE,
                contentSize.name,
            )
        }
    }

    suspend fun updateAlwaysShowSensitiveContent(always: Boolean) {
        _statusConfigFlow.value = _statusConfigFlow.value.copy(alwaysShowSensitiveContent = always)
        withContext(Dispatchers.IO) {
            localConfigManager.putBoolean(LOCAL_KEY_STATUS_ALWAYS_SHOW_SENSITIVE, always)
        }
    }

    suspend fun getDeviceId(): String {
        return localConfigManager.getStringOrPut(LOCAL_KEY_DEVICE_ID) {
            RandomIdGenerator().generateId()
        }
    }

    suspend fun getIgnoreUpdateVersion(): Long? {
        return localConfigManager.getLong(LOCAL_KEY_IGNORE_UPDATE_VERSION)
    }

    suspend fun updateIgnoreUpdateVersion(version: Long) {
        withContext(Dispatchers.IO) {
            localConfigManager.putLong(LOCAL_KEY_IGNORE_UPDATE_VERSION, version)
        }
    }
}

val LocalFreadConfigManager =
    staticCompositionLocalOf<FreadConfigManager> { error("No FreadConfigManager provided") }
