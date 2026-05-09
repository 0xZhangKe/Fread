package com.zhangke.fread.profile.screen.setting.behavior

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.TimelineDefaultPosition
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.preference.FollowingFeedPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BehaviorSettingsViewModel(
    private val freadConfigManager: FreadConfigManager,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        BehaviorSettingsUiState(
            autoPlayInlineVideo = freadConfigManager.autoPlayInlineVideo,
            alwaysShowSensitiveContent = false,
            timelineDefaultPosition = TimelineDefaultPosition.NEWEST,
            openUrlInAppBrowser = freadConfigManager.openUrlInAppBrowser,
            jumpToProfile = freadConfigManager.jumpToProfile,
        )
    )
    val uiState = _uiState.asStateFlow()

    private var followingFeedAccounts: List<LoggedAccount> = emptyList()

    init {
        viewModelScope.launch {
            freadConfigManager.getTimelineDefaultPosition()
                .let { position ->
                    _uiState.update { it.copy(timelineDefaultPosition = position) }
                }
        }
        viewModelScope.launch {
            freadConfigManager.statusConfigFlow.collect { config ->
                _uiState.update {
                    it.copy(alwaysShowSensitiveContent = config.alwaysShowSensitiveContent)
                }
            }
        }
        viewModelScope.launch { loadFollowingFeedPrefs() }
    }

    private suspend fun loadFollowingFeedPrefs() {
        val accounts = statusProvider.accountManager.getAllLoggedAccount()
        val supported = accounts.mapNotNull { account ->
            val prefs = statusProvider.feedPreferencesProvider
                .getFollowingFeedPrefs(account)
                ?.getOrNull()
            if (prefs != null) account to prefs else null
        }
        if (supported.isEmpty()) return
        followingFeedAccounts = supported.map { it.first }
        val firstPrefs = supported.first().second
        _uiState.update {
            it.copy(
                showFollowingFeedSection = true,
                followingFeedPrefs = firstPrefs,
            )
        }
    }

    fun onChangeAutoPlayInlineVideo(on: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateAutoPlayInlineVideo(on)
            _uiState.update { it.copy(autoPlayInlineVideo = on) }
        }
    }

    fun onAlwaysShowSensitiveContentChanged(always: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateAlwaysShowSensitiveContent(always)
        }
    }

    fun onTimelineDefaultPositionChanged(position: TimelineDefaultPosition) {
        viewModelScope.launch {
            freadConfigManager.updateTimelineDefaultPosition(position)
            _uiState.update { it.copy(timelineDefaultPosition = position) }
        }
    }

    fun onOpenUrlInAppBrowserChanged(openInApp: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateOpenUrlInAppBrowser(openInApp)
            _uiState.update { it.copy(openUrlInAppBrowser = openInApp) }
        }
    }

    fun onJumpToProfileChanged(on: Boolean) {
        viewModelScope.launch {
            freadConfigManager.updateJumpToProfile(on)
            _uiState.update { it.copy(jumpToProfile = on) }
        }
    }

    fun onFollowingFeedPrefsChanged(prefs: FollowingFeedPrefs) {
        _uiState.update { it.copy(followingFeedPrefs = prefs) }
        viewModelScope.launch {
            followingFeedAccounts.forEach { account ->
                statusProvider.feedPreferencesProvider
                    .updateFollowingFeedPrefs(account, prefs)
            }
        }
    }
}
