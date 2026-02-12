package com.zhangke.fread.profile.screen.setting.appearance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.daynight.LocalActivityDayNightHelper
import com.zhangke.fread.common.theme.ThemeType
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.profile.screen.setting.SettingItemWithPopup
import com.zhangke.fread.profile.screen.setting.SettingItemWithSwitch
import com.zhangke.fread.profile.screen.setting.displayName
import com.zhangke.fread.profile.screen.setting.modeName
import com.zhangke.fread.profile.screen.setting.sizeName
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
object AppearanceSettingsNavKey : NavKey

@Composable
fun AppearanceSettingsScreen(viewModel: AppearanceSettingsViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()

    val activityDayNightHelper = LocalActivityDayNightHelper.current
    val coroutineScope = rememberCoroutineScope()

    AppearanceSettingsContent(
        uiState = uiState,
        onBackClick = backStack::removeLastOrNull,
        onDayNightModeClick = {
            coroutineScope.launch {
                activityDayNightHelper.setMode(it)
            }
        },
        onThemeTypeChanged = viewModel::onThemeTypeChanged,
        onAmoledChanged = {
            coroutineScope.launch {
                activityDayNightHelper.setAmoledMode(it)
            }
        },
        onContentSizeChanged = viewModel::onContentSizeChanged,
        onImmersiveBarChanged = viewModel::onImmersiveBarChanged,
        onBlurAppBarStyleChanged = viewModel::onBlurAppBarStyleChanged,
        onHomeTabNextButtonVisibleChanged = viewModel::onHomeTabNextButtonVisibleChanged,
        onHomeTabRefreshButtonVisibleChanged = viewModel::onHomeTabRefreshButtonVisibleChanged,
    )
}

@Composable
private fun AppearanceSettingsContent(
    uiState: AppearanceSettingsUiState,
    onBackClick: () -> Unit,
    onDayNightModeClick: (DayNightMode) -> Unit,
    onThemeTypeChanged: (ThemeType) -> Unit,
    onAmoledChanged: (on: Boolean) -> Unit,
    onContentSizeChanged: (StatusContentSize) -> Unit,
    onImmersiveBarChanged: (on: Boolean) -> Unit,
    onBlurAppBarStyleChanged: (enabled: Boolean) -> Unit,
    onHomeTabNextButtonVisibleChanged: (on: Boolean) -> Unit,
    onHomeTabRefreshButtonVisibleChanged: (on: Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.setting_group_appearance),
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            ImmersiveNavBar(
                immersive = uiState.immersiveNavBar,
                onImmersiveBarChanged = onImmersiveBarChanged,
            )
            SolidBarBackgroundItem(
                blurEnabled = uiState.blurAppBarStyleEnabled,
                onBlurEnabledChanged = onBlurAppBarStyleChanged,
            )
            AmoledMode(
                enabled = uiState.amoledEnabled,
                onAmoledChanged = onAmoledChanged,
            )
            HomeTabNextButtonItem(
                visible = uiState.homeTabNextButtonVisible,
                onVisibleChanged = onHomeTabNextButtonVisibleChanged,
            )
            HomeTabRefreshButtonItem(
                visible = uiState.homeTabRefreshButtonVisible,
                onVisibleChanged = onHomeTabRefreshButtonVisibleChanged,
            )
            DayNightItem(
                uiState = uiState,
                onDayNightModeClick = onDayNightModeClick,
            )
            ContentSizeItem(
                contentSize = uiState.contentSize,
                onContentSizeChanged = onContentSizeChanged,
            )
            ThemeTypeItem(
                themeType = uiState.themeType,
                onThemeTypeChanged = onThemeTypeChanged,
            )
        }
    }
}

@Composable
private fun SolidBarBackgroundItem(
    blurEnabled: Boolean,
    onBlurEnabledChanged: (enabled: Boolean) -> Unit,
) {
    val checked = !blurEnabled
    SettingItemWithSwitch(
        icon = Icons.Default.BlurOn,
        title = stringResource(LocalizedString.setting_item_solid_bar_background_title),
        subtitle = stringResource(LocalizedString.setting_item_solid_bar_background_subtitle),
        checked = checked,
        onCheckedChangeRequest = { onBlurEnabledChanged(!it) },
    )
}

@Composable
private fun ImmersiveNavBar(
    immersive: Boolean,
    onImmersiveBarChanged: (on: Boolean) -> Unit,
) {
    SettingItemWithSwitch(
        icon = Icons.Default.PlayCircleOutline,
        title = stringResource(LocalizedString.profileSettingImmersiveNavBar),
        subtitle = stringResource(LocalizedString.profileSettingImmersiveNavBarDesc),
        checked = immersive,
        onCheckedChangeRequest = onImmersiveBarChanged,
    )
}

@Composable
private fun AmoledMode(
    enabled: Boolean,
    onAmoledChanged: (on: Boolean) -> Unit,
) {
    SettingItemWithSwitch(
        icon = Icons.Default.DarkMode,
        title = stringResource(LocalizedString.setting_item_amoled_mode),
        subtitle = stringResource(LocalizedString.setting_item_amoled_mode_description),
        checked = enabled,
        onCheckedChangeRequest = onAmoledChanged,
    )
}

@Composable
private fun DayNightItem(
    uiState: AppearanceSettingsUiState,
    onDayNightModeClick: (DayNightMode) -> Unit,
) {
    SettingItemWithPopup(
        icon = Icons.Default.Contrast,
        title = stringResource(LocalizedString.profileSettingDarkModeTitle),
        subtitle = uiState.dayNightMode.modeName,
        dropDownItemCount = DayNightMode.entries.size,
        dropDownItemText = { DayNightMode.entries[it].modeName },
        onItemClick = { index ->
            onDayNightModeClick(DayNightMode.entries[index])
        },
    )
}

@Composable
private fun ThemeTypeItem(
    themeType: ThemeType,
    onThemeTypeChanged: (ThemeType) -> Unit,
) {
    SettingItemWithPopup(
        icon = Icons.Default.Palette,
        title = stringResource(LocalizedString.profileSettingThemeTitle),
        subtitle = themeType.displayName,
        dropDownItemCount = ThemeType.entries.size,
        dropDownItemText = { ThemeType.entries[it].displayName },
        onItemClick = {
            onThemeTypeChanged(ThemeType.entries[it])
        }
    )
}

@Composable
private fun ContentSizeItem(
    contentSize: StatusContentSize,
    onContentSizeChanged: (StatusContentSize) -> Unit,
) {
    SettingItemWithPopup(
        icon = Icons.Default.TextFields,
        title = stringResource(LocalizedString.profileSettingFontSize),
        subtitle = contentSize.sizeName,
        dropDownItemCount = StatusContentSize.entries.size,
        dropDownItemText = { StatusContentSize.entries[it].sizeName },
        onItemClick = {
            onContentSizeChanged(StatusContentSize.entries[it])
        }
    )
}

@Composable
private fun HomeTabNextButtonItem(
    visible: Boolean,
    onVisibleChanged: (on: Boolean) -> Unit,
) {
    SettingItemWithSwitch(
        icon = Icons.Default.ViewTimeline,
        title = stringResource(LocalizedString.setting_item_home_tab_next_title),
        subtitle = stringResource(LocalizedString.setting_item_home_tab_next_subtitle),
        checked = visible,
        onCheckedChangeRequest = onVisibleChanged,
    )
}

@Composable
private fun HomeTabRefreshButtonItem(
    visible: Boolean,
    onVisibleChanged: (on: Boolean) -> Unit,
) {
    SettingItemWithSwitch(
        icon = Icons.Default.Refresh,
        title = stringResource(LocalizedString.setting_item_home_tab_refresh_title),
        subtitle = stringResource(LocalizedString.setting_item_home_tab_refresh_subtitle),
        checked = visible,
        onCheckedChangeRequest = onVisibleChanged,
    )
}
