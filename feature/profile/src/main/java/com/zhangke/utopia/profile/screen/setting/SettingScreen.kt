package com.zhangke.utopia.profile.screen.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LogoDev
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
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
        )
    }

    @Composable
    private fun SettingContent(
        uiState: SettingUiState,
        onBackClick: () -> Unit,
        onOpenSourceClick: () -> Unit,
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
                SettingItem(
                    icon = Icons.Default.LogoDev,
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
}
