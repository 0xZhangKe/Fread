package com.zhangke.fread.common.config

import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.fread.common.theme.ThemeType
import com.zhangke.fread.common.utils.RandomIdGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class FreadConfigManager(
    private val localConfigManager: LocalConfigManager,
) {
    companion object {
        private const val LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO = "auto_play_inline_video"
        private const val LOCAL_KEY_STATUS_CONTENT_SIZE = "fread_status_content_size"
        private const val LOCAL_KEY_STATUS_ALWAYS_SHOW_SENSITIVE =
            "fread_status_always_show_sensitive"
        private const val LOCAL_KEY_IMMERSIVE_NAV_BAR = "immersiveNavBar"
        private const val LOCAL_KEY_HOME_TAB_NEXT_BUTTON_VISIBLE = "home_tab_next_button_visible"
        private const val LOCAL_KEY_HOME_TAB_REFRESH_BUTTON_VISIBLE =
            "home_tab_refresh_button_visible"
        private const val LOCAL_KEY_DEVICE_ID = "device_id"
        private const val LOCAL_KEY_IGNORE_UPDATE_VERSION = "ignore_update_version"
        private const val LOCAL_KEY_BSKY_PUBLISH_LAN = "bsky_publish_lan"
        private const val LOCAL_KEY_LAST_SELECTED_ACCOUNT = "last_selected_account"
        private const val LOCAL_KEY_TIMELINE_DEFAULT_POSITION = "timeline_default_position"
        private const val LOCAL_KEY_THEME_TYPE = "theme_type"
        private const val LOCAL_KEY_OPEN_URL_IN_APP_BROWSER = "open_url_in_app_browser"
        private const val LOCAL_KEY_ENABLE_BLUR_APP_BAR_STYLE = "enable_blur_app_bar_style"
        private const val LOCAL_KEY_JUMP_TO_PROFILE = "jump_to_profile"
        private const val LOCAL_KEY_ALT_TEXT_PROMPT = "alt_text_prompt"

        private const val LOCAL_KEY_TRANSLATE_TARGET_LANGUAGE = "translate_target_language"

        private const val LOCAL_KEY_TRANSLATE_PROMPT = "translate_prompt"

        const val DEFAULT_ALT_TEXT_PROMPT =
            "Write alt text for this image. Be concise — 1-2 sentences for simple images. " +
                    "If the image contains readable text, transcribe it rather than describing it. " +
                    "Only describe what you can clearly see; do not guess at names or details."
    }

    val statusConfigFlow: StateFlow<StatusConfig>
        field = MutableStateFlow(StatusConfig.default())

    private val _themeTypeeFlow = MutableStateFlow(ThemeType.DEFAULT)
    val themeTypeFlow get() = _themeTypeeFlow

    val homeTabNextButtonVisibleFlow: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val homeTabRefreshButtonVisibleFlow: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val enableBlurAppBarStyleFlow: StateFlow<Boolean>
        field = MutableStateFlow(true)

    var autoPlayInlineVideo: Boolean = false
        private set
    var openUrlInAppBrowser: Boolean = true
        private set
    var jumpToProfile: Boolean = false
        private set

    suspend fun initConfig() {
        statusConfigFlow.value = readLocalStatusConfig()
        _themeTypeeFlow.value = getThemeType()
        autoPlayInlineVideo =
            localConfigManager.getBoolean(LOCAL_KEY_AUTO_PLAY_INLINE_VIDEO) ?: false
        openUrlInAppBrowser =
            localConfigManager.getBoolean(LOCAL_KEY_OPEN_URL_IN_APP_BROWSER) != false
        jumpToProfile =
            localConfigManager.getBoolean(LOCAL_KEY_JUMP_TO_PROFILE) ?: true
        enableBlurAppBarStyleFlow.value =
            localConfigManager.getBoolean(LOCAL_KEY_ENABLE_BLUR_APP_BAR_STYLE) != false
        homeTabNextButtonVisibleFlow.value =
            localConfigManager.getBoolean(LOCAL_KEY_HOME_TAB_NEXT_BUTTON_VISIBLE) ?: false
        homeTabRefreshButtonVisibleFlow.value =
            localConfigManager.getBoolean(LOCAL_KEY_HOME_TAB_REFRESH_BUTTON_VISIBLE) ?: false
    }

    private suspend fun readLocalStatusConfig(): StatusConfig {
        val alwaysShowSensitiveContent =
            localConfigManager.getBoolean(LOCAL_KEY_STATUS_ALWAYS_SHOW_SENSITIVE) == true
        val contentSize = localConfigManager.getString(LOCAL_KEY_STATUS_CONTENT_SIZE)
            ?.toContentSize()
            ?: StatusContentSize.default()
        val immersiveNavBar = localConfigManager.getBoolean(LOCAL_KEY_IMMERSIVE_NAV_BAR) != false
        return StatusConfig(
            alwaysShowSensitiveContent = alwaysShowSensitiveContent,
            contentSize = contentSize,
            immersiveNavBar = immersiveNavBar,
        )
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

    suspend fun updateOpenUrlInAppBrowser(value: Boolean) {
        openUrlInAppBrowser = value
        withContext(Dispatchers.IO) {
            localConfigManager.putBoolean(LOCAL_KEY_OPEN_URL_IN_APP_BROWSER, value)
        }
    }

    suspend fun updateJumpToProfile(value: Boolean) {
        jumpToProfile = value
        withContext(Dispatchers.IO) {
            localConfigManager.putBoolean(LOCAL_KEY_JUMP_TO_PROFILE, value)
        }
    }

    suspend fun updateEnableBlurAppBarStyle(value: Boolean) {
        enableBlurAppBarStyleFlow.value = value
        withContext(Dispatchers.IO) {
            localConfigManager.putBoolean(LOCAL_KEY_ENABLE_BLUR_APP_BAR_STYLE, value)
        }
    }

    suspend fun updateStatusContentSize(contentSize: StatusContentSize) {
        statusConfigFlow.value = statusConfigFlow.value.copy(contentSize = contentSize)
        withContext(Dispatchers.IO) {
            localConfigManager.putString(
                LOCAL_KEY_STATUS_CONTENT_SIZE,
                contentSize.name,
            )
        }
    }

    suspend fun updateAlwaysShowSensitiveContent(always: Boolean) {
        statusConfigFlow.value = statusConfigFlow.value.copy(alwaysShowSensitiveContent = always)
        withContext(Dispatchers.IO) {
            localConfigManager.putBoolean(LOCAL_KEY_STATUS_ALWAYS_SHOW_SENSITIVE, always)
        }
    }

    suspend fun updateImmersiveNavBar(immersive: Boolean) {
        statusConfigFlow.value = statusConfigFlow.value.copy(immersiveNavBar = immersive)
        withContext(Dispatchers.IO) {
            localConfigManager.putBoolean(LOCAL_KEY_IMMERSIVE_NAV_BAR, immersive)
        }
    }

    suspend fun updateHomeTabNextButtonVisible(visible: Boolean) {
        homeTabNextButtonVisibleFlow.value = visible
        withContext(Dispatchers.IO) {
            localConfigManager.putBoolean(LOCAL_KEY_HOME_TAB_NEXT_BUTTON_VISIBLE, visible)
        }
    }

    suspend fun updateHomeTabRefreshButtonVisible(visible: Boolean) {
        homeTabRefreshButtonVisibleFlow.value = visible
        withContext(Dispatchers.IO) {
            localConfigManager.putBoolean(LOCAL_KEY_HOME_TAB_REFRESH_BUTTON_VISIBLE, visible)
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

    suspend fun getBskyPublishLanguage(): List<String> {
        return localConfigManager.getString(LOCAL_KEY_BSKY_PUBLISH_LAN)?.split(",") ?: emptyList()
    }

    suspend fun updateBskyPublishLanguage(languages: List<String>) {
        withContext(Dispatchers.IO) {
            localConfigManager.putString(LOCAL_KEY_BSKY_PUBLISH_LAN, languages.joinToString(","))
        }
    }

    suspend fun getLastSelectedAccount(): String? {
        return localConfigManager.getString(LOCAL_KEY_LAST_SELECTED_ACCOUNT)
    }

    suspend fun updateLastSelectedAccount(accountUri: String) {
        localConfigManager.putString(LOCAL_KEY_LAST_SELECTED_ACCOUNT, accountUri)
    }

    suspend fun getTimelineDefaultPosition(): TimelineDefaultPosition {
        return localConfigManager.getString(LOCAL_KEY_TIMELINE_DEFAULT_POSITION)
            ?.let { TimelineDefaultPosition.valueOf(it) }
            ?: TimelineDefaultPosition.NEWEST
    }

    suspend fun updateTimelineDefaultPosition(position: TimelineDefaultPosition) {
        localConfigManager.putString(LOCAL_KEY_TIMELINE_DEFAULT_POSITION, position.name)
    }

    suspend fun getThemeType(): ThemeType {
        return localConfigManager.getString(LOCAL_KEY_THEME_TYPE)
            ?.let { ThemeType.valueOf(it) }
            ?: ThemeType.DEFAULT
    }

    suspend fun updateThemeType(type: ThemeType) {
        _themeTypeeFlow.value = type
        localConfigManager.putString(LOCAL_KEY_THEME_TYPE, type.name)
    }

    suspend fun getAltTextPrompt(): String =
        localConfigManager.getString(LOCAL_KEY_ALT_TEXT_PROMPT)
            ?.takeIf { it.isNotBlank() }
            ?: DEFAULT_ALT_TEXT_PROMPT

    suspend fun updateAltTextPrompt(value: String) {
        withContext(Dispatchers.IO) {
            localConfigManager.putString(LOCAL_KEY_ALT_TEXT_PROMPT, value)
        }
    }

    suspend fun getTranslateTargetLanguage(): String? {
        return localConfigManager.getString(LOCAL_KEY_TRANSLATE_TARGET_LANGUAGE)
    }

    suspend fun updateTranslateTargetLanguage(language: String) {
        withContext(Dispatchers.IO) {
            localConfigManager.putString(LOCAL_KEY_TRANSLATE_TARGET_LANGUAGE, language)
        }
    }

    suspend fun getTranslatePrompt(): String? {
        return localConfigManager.getString(LOCAL_KEY_TRANSLATE_PROMPT)
    }

    suspend fun updateTranslatePrompt(prompt: String) {
        withContext(Dispatchers.IO) {
            localConfigManager.putString(LOCAL_KEY_TRANSLATE_PROMPT, prompt)
        }
    }
}

val LocalFreadConfigManager =
    staticCompositionLocalOf<FreadConfigManager> { error("No FreadConfigManager provided") }
