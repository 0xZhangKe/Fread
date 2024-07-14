package com.zhangke.fread.profile.screen.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LogoDev
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.utils.SystemPageUtils
import com.zhangke.fread.analytics.SettingElements
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.daynight.DayNightMode
import com.zhangke.fread.common.language.LanguageSettingType
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.profile.R
import com.zhangke.fread.profile.screen.opensource.OpenSourceScreen
import com.zhangke.fread.profile.screen.setting.about.AboutScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SettingScreenModel>()
        val uiState by viewModel.uiState.collectAsState()
        val context = LocalContext.current
        SettingContent(
            uiState = uiState,
            onBackClick = navigator::pop,
            onOpenSourceClick = {
                reportClick(SettingElements.OPEN_SOURCE)
                navigator.push(OpenSourceScreen())
            },
            onDayNightModeClick = {
                reportClick(SettingElements.DARK_MODE) {
                    put("mode", it.name)
                }
                viewModel.onChangeDayNightMode(it)
            },
            onLanguageClick = {
                reportClick(SettingElements.LANGUAGE) {
                    put("language", it.name)
                }
                viewModel.onLanguageClick(context, it)
            },
            onRatingClick = {
                reportClick(SettingElements.RATTING)
                SystemPageUtils.openAppMarket(context)
            },
            onAboutClick = {
                reportClick(SettingElements.ABOUT)
                navigator.push(AboutScreen())
            },
        )
    }

    @Composable
    private fun SettingContent(
        uiState: SettingUiState,
        onBackClick: () -> Unit,
        onOpenSourceClick: () -> Unit,
        onDayNightModeClick: (DayNightMode) -> Unit,
        onLanguageClick: (LanguageSettingType) -> Unit,
        onRatingClick: () -> Unit,
        onAboutClick: () -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = "Setting",
                    onBackClick = onBackClick,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding),
            ) {
                DarNightItem(
                    uiState = uiState,
                    onDayNightModeClick = onDayNightModeClick,
                )
                LanguageItem(
                    uiState = uiState,
                    onLanguageClick = onLanguageClick,
                )
                SettingItem(
                    icon = Icons.Default.Code,
                    title = stringResource(R.string.profile_setting_open_source_title),
                    subtitle = stringResource(R.string.profile_setting_open_source_desc),
                    onClick = onOpenSourceClick,
                )
                SettingItem(
                    icon = {
                        Icon(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(2.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_ratting),
                            contentDescription = stringResource(R.string.profile_setting_ratting),
                        )
                    },
                    title = stringResource(R.string.profile_setting_ratting),
                    subtitle = stringResource(R.string.profile_setting_ratting_desc),
                    onClick = onRatingClick,
                )
                SettingItem(
                    icon = Icons.Default.LogoDev,
                    title = stringResource(R.string.profile_setting_about_title),
                    subtitle = uiState.settingInfo,
                    onClick = onAboutClick,
                )
            }
        }
    }

    @Composable
    private fun DarNightItem(
        uiState: SettingUiState,
        onDayNightModeClick: (DayNightMode) -> Unit,
    ) {
        SettingItemWithPopup(
            icon = Icons.Default.LogoDev,
            title = stringResource(R.string.profile_setting_dark_mode_title),
            subtitle = uiState.dayNightMode.modeName,
            dropDownItems = DayNightMode.entries.map { it.modeName },
            onItemClick = { index ->
                onDayNightModeClick(DayNightMode.entries[index])
            },
        )
    }

    @Composable
    private fun LanguageItem(
        uiState: SettingUiState,
        onLanguageClick: (LanguageSettingType) -> Unit,
    ) {
        SettingItemWithPopup(
            icon = Icons.Default.Language,
            title = stringResource(R.string.profile_setting_language_title),
            subtitle = uiState.languageSettingType.typeName,
            dropDownItems = LanguageSettingType.entries.map { it.typeName },
            onItemClick = {
                onLanguageClick(LanguageSettingType.entries[it])
            }
        )
    }

    @Composable
    private fun SettingItemWithPopup(
        icon: ImageVector,
        title: String,
        subtitle: String,
        dropDownItems: List<String>,
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
                dropDownItems.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(item) },
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
            subtitle = subtitle,
            onClick = onClick,
        )
    }

    @Composable
    private fun SettingItem(
        icon: @Composable () -> Unit,
        title: String,
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
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon()
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                    )
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                    )
                }
            }
        }
    }

    private val LanguageSettingType.typeName: String
        @Composable get() {
            return when (this) {
                LanguageSettingType.CN -> stringResource(R.string.profile_setting_language_zh)
                LanguageSettingType.EN -> stringResource(R.string.profile_setting_language_en)
                LanguageSettingType.SYSTEM -> stringResource(R.string.profile_setting_language_system)
            }
        }

    private val DayNightMode.modeName: String
        @Composable get() {
            return when (this) {
                DayNightMode.NIGHT -> stringResource(R.string.profile_setting_dark_mode_dark)
                DayNightMode.DAY -> stringResource(R.string.profile_setting_dark_mode_light)
                DayNightMode.FOLLOW_SYSTEM -> stringResource(R.string.profile_setting_dark_mode_follow_system)
            }
        }
}
