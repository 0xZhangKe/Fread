package com.zhangke.fread.profile.screen.setting.behavior

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.common.config.TimelineDefaultPosition
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.profile.screen.setting.SettingItemWithPopup
import com.zhangke.fread.profile.screen.setting.SettingItemWithSwitch
import com.zhangke.fread.profile.screen.setting.displayName
import com.zhangke.fread.status.preference.FollowingFeedPrefs
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
        onJumpToProfileChanged = viewModel::onJumpToProfileChanged,
        onFollowingFeedPrefsChanged = viewModel::onFollowingFeedPrefsChanged,
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
    onJumpToProfileChanged: (Boolean) -> Unit,
    onFollowingFeedPrefsChanged: (FollowingFeedPrefs) -> Unit,
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
            JumpToProfileItem(
                jumpToProfile = uiState.jumpToProfile,
                onJumpToProfileChanged = onJumpToProfileChanged,
            )
            if (uiState.showFollowingFeedSection) {
                FollowingFeedSection(
                    prefs = uiState.followingFeedPrefs,
                    onPrefsChanged = onFollowingFeedPrefsChanged,
                )
            }
        }
    }
}

@Composable
private fun JumpToProfileItem(
    jumpToProfile: Boolean,
    onJumpToProfileChanged: (on: Boolean) -> Unit,
) {
    SettingItemWithSwitch(
        icon = Icons.Default.AccountCircle,
        title = stringResource(LocalizedString.setting_item_jump_to_profile_title),
        subtitle = stringResource(LocalizedString.setting_item_jump_to_profile_subtitle),
        checked = jumpToProfile,
        onCheckedChangeRequest = onJumpToProfileChanged,
    )
}

@Composable
private fun FollowingFeedSection(
    prefs: FollowingFeedPrefs,
    onPrefsChanged: (FollowingFeedPrefs) -> Unit,
) {
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider()
    Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)) {
        Text(
            text = stringResource(LocalizedString.setting_group_following_feed_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = stringResource(LocalizedString.setting_group_following_feed_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
    SettingItemWithSwitch(
        icon = Icons.AutoMirrored.Filled.Reply,
        title = stringResource(LocalizedString.setting_item_hide_replies_title),
        subtitle = stringResource(LocalizedString.setting_item_hide_replies_subtitle),
        checked = prefs.hideReplies,
        onCheckedChangeRequest = { onPrefsChanged(prefs.copy(hideReplies = it)) },
    )
    SettingItemWithSwitch(
        icon = Icons.Default.PersonOff,
        title = stringResource(LocalizedString.setting_item_hide_replies_by_unfollowed_title),
        subtitle = stringResource(LocalizedString.setting_item_hide_replies_by_unfollowed_subtitle),
        checked = prefs.hideRepliesByUnfollowed,
        onCheckedChangeRequest = { onPrefsChanged(prefs.copy(hideRepliesByUnfollowed = it)) },
    )
    SettingItemWithSwitch(
        icon = Icons.Default.Repeat,
        title = stringResource(LocalizedString.setting_item_hide_reposts_title),
        subtitle = stringResource(LocalizedString.setting_item_hide_reposts_subtitle),
        checked = prefs.hideReposts,
        onCheckedChangeRequest = { onPrefsChanged(prefs.copy(hideReposts = it)) },
    )
    SettingItemWithSwitch(
        icon = Icons.Default.FormatQuote,
        title = stringResource(LocalizedString.setting_item_hide_quote_posts_title),
        subtitle = stringResource(LocalizedString.setting_item_hide_quote_posts_subtitle),
        checked = prefs.hideQuotePosts,
        onCheckedChangeRequest = { onPrefsChanged(prefs.copy(hideQuotePosts = it)) },
    )
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
