package com.zhangke.utopia.profile.screen.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.utopia.common.daynight.DayNightMode
import com.zhangke.utopia.profile.R
import com.zhangke.utopia.profile.screen.opensource.OpenSourceScreen

class SettingScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SettingScreenModel>()
        val uiState by viewModel.uiState.collectAsState()
        SettingContent(
            uiState = uiState,
            onBackClick = navigator::pop,
            onOpenSourceClick = {
                navigator.push(OpenSourceScreen())
            },
            onDayNightModeClick = viewModel::onChangeDayNightMode,
        )
    }

    @Composable
    private fun SettingContent(
        uiState: SettingUiState,
        onBackClick: () -> Unit,
        onOpenSourceClick: () -> Unit,
        onDayNightModeClick: (DayNightMode) -> Unit,
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
                SettingItem(
                    icon = Icons.Default.Code,
                    title = stringResource(R.string.profile_setting_open_source_title),
                    subtitle = stringResource(R.string.profile_setting_open_source_desc),
                    onClick = onOpenSourceClick,
                )
                SettingItem(
                    icon = Icons.Default.LogoDev,
                    title = stringResource(R.string.profile_setting_about_title),
                    subtitle = uiState.settingInfo,
                    onClick = {},
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
        ) {
            DayNightMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.modeName) },
                    onClick = {
                        onDayNightModeClick(mode)
                    },
                )
            }
        }
    }

    @Composable
    private fun SettingItemWithPopup(
        icon: ImageVector,
        title: String,
        subtitle: String,
        dropDownItems: @Composable ColumnScope.() -> Unit,
    ) {
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
                dropDownItems()
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
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                )
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

    private val DayNightMode.modeName: String
        @Composable get() {
            return when (this) {
                DayNightMode.NIGHT -> stringResource(R.string.profile_setting_dark_mode_dark)
                DayNightMode.DAY -> stringResource(R.string.profile_setting_dark_mode_light)
                DayNightMode.FOLLOW_SYSTEM -> stringResource(R.string.profile_setting_dark_mode_follow_system)
            }
        }
}
