package com.zhangke.fread.profile.screen.setting.ai.translate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.localization.LocalizedString
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Serializable
object TranslateSettingNavKey : NavKey

@Composable
fun TranslateSettingScreen() {
    val backStack = LocalNavBackStack.currentOrThrow
    val viewModel: TranslateSettingViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    TranslateSettingContent(
        uiState = uiState,
        onBackClick = backStack::removeLastOrNull,
    )
}

@Composable
private fun TranslateSettingContent(
    uiState: TranslateSettingUiState,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.translation_settings_title),
                onBackClick = onBackClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding),
        ) {
//stringResource(LocalizedString.notSetYet)
        }
    }
}
