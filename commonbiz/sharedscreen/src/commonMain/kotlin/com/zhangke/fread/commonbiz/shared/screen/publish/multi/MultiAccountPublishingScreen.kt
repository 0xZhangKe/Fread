package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishTopBar

class MultiAccountPublishingScreen : BaseScreen() {

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

            }
        }
    }
}
