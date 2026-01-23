package com.zhangke.fread.profile.screen.setting.behavior

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.common.config.TimelineDefaultPosition
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.profile.screen.setting.SettingItemWithPopup
import com.zhangke.fread.profile.screen.setting.SettingItemWithSwitch
import com.zhangke.fread.profile.screen.setting.displayName
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
object BehaviorSettingsNavKey : NavKey

@Composable
fun BehaviorSettingsScreen(viewModel: BehaviorSettingsViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()

    BehaviorSettingsContent(
        uiState = uiState,
        onBackClick = backStack::removeLastOrNull,
        onSwitchAutoPlayClick = viewModel::onChangeAutoPlayInlineVideo,
        onAlwaysShowSensitive = viewModel::onAlwaysShowSensitiveContentChanged,
        onTimelineDefaultPositionChanged = viewModel::onTimelineDefaultPositionChanged,
        onOpenUrlInAppBrowserChanged = viewModel::onOpenUrlInAppBrowserChanged,
    )
}

@Composable
private fun BehaviorSettingsContent(
    uiState: BehaviorSettingsUiState,
    onBackClick: () -> Unit,
    onSwitchAutoPlayClick: (on: Boolean) -> Unit,
    onAlwaysShowSensitive: (Boolean) -> Unit,
    onTimelineDefaultPositionChanged: (TimelineDefaultPosition) -> Unit,
    onOpenUrlInAppBrowserChanged: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.setting_group_behavior),
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            AutoPlayInlineVideoItem(
                autoPlay = uiState.autoPlayInlineVideo,
                onSwitchClick = onSwitchAutoPlayClick,
            )
            AlwaysShowSensitiveContentItem(
                alwaysShowing = uiState.alwaysShowSensitiveContent,
                onAlwaysChanged = onAlwaysShowSensitive,
            )
            OpenUrlBySystemBrowserItem(
                openUrlBySystem = !uiState.openUrlInAppBrowser,
                onOpenUrlBySystemChanged = { onOpenUrlInAppBrowserChanged(!it) },
            )
            TimelinePositionItem(
                position = uiState.timelineDefaultPosition,
                onPositionChanged = onTimelineDefaultPositionChanged,
            )
        }
    }
}

@Composable
private fun AutoPlayInlineVideoItem(
    autoPlay: Boolean,
    onSwitchClick: (on: Boolean) -> Unit,
) {
    SettingItemWithSwitch(
        icon = Icons.Default.PlayCircleOutline,
        title = stringResource(LocalizedString.profileSettingInlineVideoAutoPlay),
        subtitle = stringResource(LocalizedString.profileSettingInlineVideoAutoPlaySubtitle),
        checked = autoPlay,
        onCheckedChangeRequest = onSwitchClick,
    )
}

@Composable
private fun AlwaysShowSensitiveContentItem(
    alwaysShowing: Boolean,
    onAlwaysChanged: (on: Boolean) -> Unit,
) {
    SettingItemWithSwitch(
        icon = Icons.Default.PlayCircleOutline,
        title = stringResource(LocalizedString.profileSettingAlwaysShowSensitiveContent),
        subtitle = stringResource(LocalizedString.profileSettingAlwaysShowSensitiveContentSubtitle),
        checked = alwaysShowing,
        onCheckedChangeRequest = onAlwaysChanged,
    )
}

@Composable
private fun TimelinePositionItem(
    position: TimelineDefaultPosition,
    onPositionChanged: (TimelineDefaultPosition) -> Unit,
) {
    SettingItemWithPopup(
        icon = Icons.Default.ViewTimeline,
        title = stringResource(LocalizedString.profileSettingTimelinePosition),
        subtitle = position.displayName,
        dropDownItemCount = TimelineDefaultPosition.entries.size,
        dropDownItemText = {
            TimelineDefaultPosition.entries[it].displayName
        },
        onItemClick = {
            onPositionChanged(TimelineDefaultPosition.entries[it])
        },
    )
}

@Composable
private fun OpenUrlBySystemBrowserItem(
    openUrlBySystem: Boolean,
    onOpenUrlBySystemChanged: (on: Boolean) -> Unit,
) {
    SettingItemWithSwitch(
        icon = Icons.Default.OpenInBrowser,
        title = stringResource(LocalizedString.setting_item_open_url_by_system_title),
        subtitle = stringResource(LocalizedString.setting_item_open_url_by_system_subtitle),
        checked = openUrlBySystem,
        onCheckedChangeRequest = onOpenUrlBySystemChanged,
    )
}
