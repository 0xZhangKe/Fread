package com.zhangke.fread.profile.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.config.TimelineDefaultPosition
import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.daynight.LocalActivityDayNightHelper
import com.zhangke.fread.common.handler.LocalTextHandler
import com.zhangke.fread.common.language.LanguageSettingItem
import com.zhangke.fread.common.language.LocalActivityLanguageHelper
import com.zhangke.fread.common.theme.ThemeType
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.ic_code
import com.zhangke.fread.feature.profile.ic_ratting
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.profile.screen.donate.DonateScreenNavKey
import com.zhangke.fread.profile.screen.opensource.OpenSourceScreenNavKey
import com.zhangke.fread.profile.screen.setting.about.AboutScreenNavKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Serializable
object SettingScreenNavKey : NavKey

private val itemHeight = 82.dp

@Composable
fun SettingScreen(viewModel: SettingScreenModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()

    val activityLanguageHelper = LocalActivityLanguageHelper.current
    val activityDayNightHelper = LocalActivityDayNightHelper.current
    val activityTextHandler = LocalTextHandler.current

    val coroutineScope = rememberCoroutineScope()
    SettingContent(
        uiState = uiState,
        onBackClick = backStack::removeLastOrNull,
        onOpenSourceClick = {
            backStack.add(OpenSourceScreenNavKey)
        },
        onSwitchAutoPlayClick = {
            viewModel.onChangeAutoPlayInlineVideo(it)
        },
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
        onLanguageClick = {
            activityLanguageHelper.setLanguage(it)
        },
        onRatingClick = {
            activityTextHandler.openAppMarket()
        },
        onAboutClick = {
            backStack.add(AboutScreenNavKey)
        },
        onDonateClick = {
            backStack.add(DonateScreenNavKey)
        },
        onContentSizeChanged = {
            viewModel.onContentSizeChanged(it)
        },
        onAlwaysShowSensitive = viewModel::onAlwaysShowSensitiveContentChanged,
        onImmersiveBarChanged = viewModel::onImmersiveBarChanged,
        onTimelineDefaultPositionChanged = viewModel::onTimelineDefaultPositionChanged,
    )
}

@Composable
private fun SettingContent(
    uiState: SettingUiState,
    onBackClick: () -> Unit,
    onSwitchAutoPlayClick: (on: Boolean) -> Unit,
    onOpenSourceClick: () -> Unit,
    onDayNightModeClick: (DayNightMode) -> Unit,
    onThemeTypeChanged: (ThemeType) -> Unit,
    onLanguageClick: (LanguageSettingItem) -> Unit,
    onImmersiveBarChanged: (on: Boolean) -> Unit,
    onAmoledChanged: (on: Boolean) -> Unit,
    onRatingClick: () -> Unit,
    onAboutClick: () -> Unit,
    onDonateClick: () -> Unit,
    onContentSizeChanged: (StatusContentSize) -> Unit,
    onAlwaysShowSensitive: (Boolean) -> Unit,
    onTimelineDefaultPositionChanged: (TimelineDefaultPosition) -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.settings),
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
            ImmersiveNavBar(
                immersive = uiState.immersiveNavBar,
                onImmersiveBarChanged = onImmersiveBarChanged,
            )
            AmoledMode(
                enabled = uiState.amoledEnabled,
                onAmoledChanged = onAmoledChanged,
            )
            DayNightItem(
                uiState = uiState,
                onDayNightModeClick = onDayNightModeClick,
            )
            ContentSizeItem(
                contentSize = uiState.contentSize,
                onContentSizeChanged = onContentSizeChanged,
            )
            TimelinePositionItem(
                position = uiState.timelineDefaultPosition,
                onPositionChanged = onTimelineDefaultPositionChanged,
            )
            ThemeTypeItem(
                themeType = uiState.themeType,
                onThemeTypeChanged = onThemeTypeChanged,
            )
            LanguageItem(
                onLanguageClick = onLanguageClick,
            )
            FeedbackItem()
            SettingItem(
                icon = vectorResource(Res.drawable.ic_code),
                title = stringResource(LocalizedString.profileSettingOpenSourceTitle),
                subtitle = stringResource(LocalizedString.profileSettingOpenSourceDesc),
                onClick = onOpenSourceClick,
            )
            SettingItem(
                icon = {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(2.dp),
                        imageVector = vectorResource(Res.drawable.ic_ratting),
                        contentDescription = stringResource(LocalizedString.profileSettingRatting),
                    )
                },
                title = stringResource(LocalizedString.profileSettingRatting),
                subtitle = stringResource(LocalizedString.profileSettingRattingDesc),
                onClick = onRatingClick,
            )
            SettingItem(
                icon = Icons.Outlined.Coffee,
                title = stringResource(LocalizedString.donate),
                subtitle = stringResource(LocalizedString.profileSettingDonateDesc),
                onClick = onDonateClick,
            )
            SettingItem(
                icon = Icons.Outlined.Info,
                title = stringResource(LocalizedString.profileSettingAboutTitle),
                subtitle = uiState.settingInfo,
                redDot = uiState.haveNewAppVersion,
                onClick = onAboutClick,
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
    uiState: SettingUiState,
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
private fun LanguageItem(
    onLanguageClick: (LanguageSettingItem) -> Unit,
) {
    val activityLanguageHelper = LocalActivityLanguageHelper.current
    val subtitle = activityLanguageHelper.currentLanguage.getDisplayName()
    SettingItemWithPopup(
        icon = Icons.Default.Language,
        title = stringResource(LocalizedString.profileSettingLanguageTitle),
        subtitle = subtitle,
        dropDownItemCount = LanguageSettingItem.items.size,
        dropDownItemText = { LanguageSettingItem.items[it].getDisplayName() },
        onItemClick = {
            onLanguageClick(LanguageSettingItem.items[it])
        }
    )
}

@Composable
private fun FeedbackItem() {
    var showFeedbackBottomSheet by remember {
        mutableStateOf(false)
    }
    SettingItem(
        icon = Icons.AutoMirrored.Outlined.Chat,
        title = stringResource(LocalizedString.profileSettingOpenSourceFeedback),
        subtitle = stringResource(LocalizedString.profileSettingOpenSourceFeedbackDesc),
        onClick = {
            showFeedbackBottomSheet = true
        },
    )
    if (showFeedbackBottomSheet) {
        FeedbackBottomSheet(
            onDismissRequest = { showFeedbackBottomSheet = false },
        )
    }
}

@Composable
private fun SettingItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChangeRequest: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1F)) {
            SettingItem(
                icon = icon,
                title = title,
                subtitle = subtitle,
                onClick = {},
            )
        }
        Switch(
            modifier = Modifier,
            checked = checked,
            onCheckedChange = onCheckedChangeRequest,
        )
        Spacer(modifier = Modifier.width(16.dp))
    }
}

@Composable
private fun SettingItemWithPopup(
    icon: ImageVector,
    title: String,
    subtitle: String,
    // dropDownItems: List<String>,
    dropDownItemCount: Int,
    dropDownItemText: @Composable (Int) -> String,
    onItemClick: (Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.fillMaxWidth()) {
        var showPopup by remember {
            mutableStateOf(false)
        }
        SettingItem(
            icon = icon,
            title = title,
            subtitle = subtitle,
            onClick = {
                showPopup = true
            },
        )
        DropdownMenu(
            expanded = showPopup,
            offset = DpOffset(x = 36.dp, y = 0.dp),
            onDismissRequest = { showPopup = false },
        ) {
            repeat(dropDownItemCount) { index ->
                DropdownMenuItem(
                    text = { Text(dropDownItemText(index)) },
                    onClick = {
                        showPopup = false
                        coroutineScope.launch {
                            delay(100)
                            onItemClick(index)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    redDot: Boolean = false,
    onClick: () -> Unit,
) {
    SettingItem(
        icon = {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = icon,
                contentDescription = title,
            )
        },
        title = title,
        redDot = redDot,
        subtitle = subtitle,
        onClick = onClick,
    )
}

@Composable
private fun SettingItem(
    icon: @Composable () -> Unit,
    title: String,
    redDot: Boolean = false,
    subtitle: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = itemHeight)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                    )
                    if (redDot) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier.size(4.dp)
                                .clip(CircleShape)
                                .background(Color.Red.copy(alpha = 0.8F)),
                        )
                    }
                }
                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
