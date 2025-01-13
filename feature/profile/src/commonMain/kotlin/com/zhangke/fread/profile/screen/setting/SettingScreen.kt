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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.TextFields
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
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.analytics.SettingElements
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.common.config.StatusContentSize
import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.daynight.LocalActivityDayNightHelper
import com.zhangke.fread.common.handler.LocalActivityTextHandler
import com.zhangke.fread.common.language.LanguageSettingType
import com.zhangke.fread.common.language.LocalActivityLanguageHelper
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.donate
import com.zhangke.fread.commonbiz.settings
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.ic_code
import com.zhangke.fread.feature.profile.ic_ratting
import com.zhangke.fread.feature.profile.profile_setting_about_title
import com.zhangke.fread.feature.profile.profile_setting_always_show_sensitive_content
import com.zhangke.fread.feature.profile.profile_setting_always_show_sensitive_content_subtitle
import com.zhangke.fread.feature.profile.profile_setting_dark_mode_dark
import com.zhangke.fread.feature.profile.profile_setting_dark_mode_follow_system
import com.zhangke.fread.feature.profile.profile_setting_dark_mode_light
import com.zhangke.fread.feature.profile.profile_setting_dark_mode_title
import com.zhangke.fread.feature.profile.profile_setting_donate_desc
import com.zhangke.fread.feature.profile.profile_setting_font_size
import com.zhangke.fread.feature.profile.profile_setting_font_size_large
import com.zhangke.fread.feature.profile.profile_setting_font_size_medium
import com.zhangke.fread.feature.profile.profile_setting_font_size_small
import com.zhangke.fread.feature.profile.profile_setting_inline_video_auto_play
import com.zhangke.fread.feature.profile.profile_setting_inline_video_auto_play_subtitle
import com.zhangke.fread.feature.profile.profile_setting_language_en
import com.zhangke.fread.feature.profile.profile_setting_language_system
import com.zhangke.fread.feature.profile.profile_setting_language_title
import com.zhangke.fread.feature.profile.profile_setting_language_zh
import com.zhangke.fread.feature.profile.profile_setting_open_source_desc
import com.zhangke.fread.feature.profile.profile_setting_open_source_feedback
import com.zhangke.fread.feature.profile.profile_setting_open_source_feedback_desc
import com.zhangke.fread.feature.profile.profile_setting_open_source_title
import com.zhangke.fread.feature.profile.profile_setting_ratting
import com.zhangke.fread.feature.profile.profile_setting_ratting_desc
import com.zhangke.fread.profile.screen.opensource.OpenSourceScreen
import com.zhangke.fread.profile.screen.setting.about.AboutScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

class SettingScreen : BaseScreen() {

    companion object {

        private val itemHeight = 82.dp
    }

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<SettingScreenModel>()
        val uiState by viewModel.uiState.collectAsState()

        val browserLauncher = LocalActivityBrowserLauncher.current
        val activityLanguageHelper = LocalActivityLanguageHelper.current
        val activityDayNightHelper = LocalActivityDayNightHelper.current
        val activityTextHandler = LocalActivityTextHandler.current

        SettingContent(
            uiState = uiState,
            onBackClick = navigator::pop,
            onOpenSourceClick = {
                reportClick(SettingElements.OPEN_SOURCE)
                navigator.push(OpenSourceScreen())
            },
            onSwitchAutoPlayClick = {
                reportClick(SettingElements.AUTO_PLAY_INLINE_VIDEO) {
                    put("on", it.toString())
                }
                viewModel.onChangeAutoPlayInlineVideo(it)
            },
            onDayNightModeClick = {
                reportClick(SettingElements.DARK_MODE) {
                    put("mode", it.name)
                }
                activityDayNightHelper.setMode(it)
            },
            onLanguageClick = {
                reportClick(SettingElements.LANGUAGE) {
                    put("language", it.name)
                }
                activityLanguageHelper.setLanguage(it)
            },
            onRatingClick = {
                reportClick(SettingElements.RATTING)
                activityTextHandler.openAppMarket()
            },
            onAboutClick = {
                reportClick(SettingElements.ABOUT)
                navigator.push(AboutScreen())
            },
            onDonateClick = {
                reportClick(SettingElements.DONATE)
                browserLauncher.launchWebTabInApp(
                    AppCommonConfig.DONATE_LINK,
                    checkAppSupportPage = false,
                )
            },
            onContentSizeChanged = {
                reportClick(SettingElements.CONTENT_SIZE)
                viewModel.onContentSizeChanged(it)
            },
            onAlwaysShowSensitive = viewModel::onAlwaysShowSensitiveContentChanged,
        )
    }

    @Composable
    private fun SettingContent(
        uiState: SettingUiState,
        onBackClick: () -> Unit,
        onSwitchAutoPlayClick: (on: Boolean) -> Unit,
        onOpenSourceClick: () -> Unit,
        onDayNightModeClick: (DayNightMode) -> Unit,
        onLanguageClick: (LanguageSettingType) -> Unit,
        onRatingClick: () -> Unit,
        onAboutClick: () -> Unit,
        onDonateClick: () -> Unit,
        onContentSizeChanged: (StatusContentSize) -> Unit,
        onAlwaysShowSensitive: (Boolean) -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(com.zhangke.fread.commonbiz.Res.string.settings),
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
                DarNightItem(
                    uiState = uiState,
                    onDayNightModeClick = onDayNightModeClick,
                )
                LanguageItem(
                    onLanguageClick = onLanguageClick,
                )
                ContentSizeItem(
                    contentSize = uiState.contentSize,
                    onContentSizeChanged = onContentSizeChanged,
                )
                FeedbackItem()
                SettingItem(
                    icon = vectorResource(Res.drawable.ic_code),
                    title = stringResource(Res.string.profile_setting_open_source_title),
                    subtitle = stringResource(Res.string.profile_setting_open_source_desc),
                    onClick = onOpenSourceClick,
                )
                SettingItem(
                    icon = {
                        Icon(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(2.dp),
                            imageVector = vectorResource(Res.drawable.ic_ratting),
                            contentDescription = stringResource(Res.string.profile_setting_ratting),
                        )
                    },
                    title = stringResource(Res.string.profile_setting_ratting),
                    subtitle = stringResource(Res.string.profile_setting_ratting_desc),
                    onClick = onRatingClick,
                )
                SettingItem(
                    icon = Icons.Outlined.Coffee,
                    title = stringResource(com.zhangke.fread.commonbiz.Res.string.donate),
                    subtitle = stringResource(Res.string.profile_setting_donate_desc),
                    onClick = onDonateClick,
                )
                SettingItem(
                    icon = Icons.Outlined.Info,
                    title = stringResource(Res.string.profile_setting_about_title),
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
            title = stringResource(Res.string.profile_setting_inline_video_auto_play),
            subtitle = stringResource(Res.string.profile_setting_inline_video_auto_play_subtitle),
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
            title = stringResource(Res.string.profile_setting_always_show_sensitive_content),
            subtitle = stringResource(Res.string.profile_setting_always_show_sensitive_content_subtitle),
            checked = alwaysShowing,
            onCheckedChangeRequest = onAlwaysChanged,
        )
    }

    @Composable
    private fun DarNightItem(
        uiState: SettingUiState,
        onDayNightModeClick: (DayNightMode) -> Unit,
    ) {
        SettingItemWithPopup(
            icon = Icons.Default.Contrast,
            title = stringResource(Res.string.profile_setting_dark_mode_title),
            subtitle = uiState.dayNightMode.modeName,
            dropDownItemCount = DayNightMode.entries.size,
            dropDownItemText = { DayNightMode.entries[it].modeName },
            onItemClick = { index ->
                onDayNightModeClick(DayNightMode.entries[index])
            },
        )
    }

    @Composable
    private fun ContentSizeItem(
        contentSize: StatusContentSize,
        onContentSizeChanged: (StatusContentSize) -> Unit,
    ) {
        SettingItemWithPopup(
            icon = Icons.Default.TextFields,
            title = stringResource(Res.string.profile_setting_font_size),
            subtitle = contentSize.sizeName,
            dropDownItemCount = StatusContentSize.entries.size,
            dropDownItemText = { StatusContentSize.entries[it].sizeName },
            onItemClick = {
                onContentSizeChanged(StatusContentSize.entries[it])
            }
        )
    }

    @Composable
    private fun LanguageItem(
        onLanguageClick: (LanguageSettingType) -> Unit,
    ) {
        val activityLanguageHelper = LocalActivityLanguageHelper.current
        SettingItemWithPopup(
            icon = Icons.Default.Language,
            title = stringResource(Res.string.profile_setting_language_title),
            subtitle = activityLanguageHelper.currentType.typeName,
            dropDownItemCount = LanguageSettingType.entries.size,
            dropDownItemText = { LanguageSettingType.entries[it].typeName },
            onItemClick = {
                onLanguageClick(LanguageSettingType.entries[it])
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
            title = stringResource(Res.string.profile_setting_open_source_feedback),
            subtitle = stringResource(Res.string.profile_setting_open_source_feedback_desc),
            onClick = {
                reportClick(SettingElements.FEEDBACK)
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
                        if (redDot){
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

    private val LanguageSettingType.typeName: String
        @Composable
        get() {
            return when (this) {
                LanguageSettingType.CN -> stringResource(Res.string.profile_setting_language_zh)
                LanguageSettingType.EN -> stringResource(Res.string.profile_setting_language_en)
                LanguageSettingType.SYSTEM -> stringResource(Res.string.profile_setting_language_system)
            }
        }

    private val DayNightMode.modeName: String
        @Composable
        get() {
            return when (this) {
                DayNightMode.NIGHT -> stringResource(Res.string.profile_setting_dark_mode_dark)
                DayNightMode.DAY -> stringResource(Res.string.profile_setting_dark_mode_light)
                DayNightMode.FOLLOW_SYSTEM -> stringResource(Res.string.profile_setting_dark_mode_follow_system)
            }
        }

    private val StatusContentSize.sizeName: String
        @Composable
        get() = when (this) {
            StatusContentSize.SMALL -> stringResource(Res.string.profile_setting_font_size_small)
            StatusContentSize.MEDIUM -> stringResource(Res.string.profile_setting_font_size_medium)
            StatusContentSize.LARGE -> stringResource(Res.string.profile_setting_font_size_large)
        }
}
