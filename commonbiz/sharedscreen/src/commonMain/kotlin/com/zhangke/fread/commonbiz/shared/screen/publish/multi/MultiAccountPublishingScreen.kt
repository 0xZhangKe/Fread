package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostFeaturesPanel
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishTopBar
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.AvatarsHorizontalStack
import com.zhangke.fread.status.account.LoggedAccount

class MultiAccountPublishingScreen : BaseScreen() {

    companion object {

        fun open(navigator: Navigator, accounts: List<LoggedAccount>) {
            accounts.map { it.uri }
        }
    }

    @Composable
    override fun Content() {
        super.Content()
    }

    @Composable
    private fun MultiAccountPublishingContent(
        uiState: MultiAccountPublishingUiState,
        snackBarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onPublishClick: () -> Unit,
        onMediaSelected: (List<PlatformUri>) -> Unit,
        onLanguageSelected: (List<String>) -> Unit,
    ) {
        Scaffold(
            topBar = {
                PublishTopBar(
                    publishing = uiState.publishing,
                    onBackClick = onBackClick,
                    onPublishClick = onPublishClick,
                )
            },
            snackbarHost = { SnackbarHost(snackBarHostState) },
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp),
                ) {
                    AvatarsHorizontalStack(
                        modifier = Modifier,
                        avatars = uiState.addedAccounts.map { it.avatar },
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    PublishPostFeaturesPanel(
                        modifier = Modifier.weight(1F),
                        contentLength = uiState.content.text.length,
                        maxContentLimit = uiState.globalRules.maxCharacters,
                        mediaAvailableCount = uiState.mediaAvailableCount,
                        onMediaSelected = onMediaSelected,
                        selectedLanguages = uiState.selectedLanguages,
                        maxLanguageCount = uiState.globalRules.maxLanguageCount,
                        onLanguageSelected = onLanguageSelected,
                    )
                }
            }
        }
    }
}
