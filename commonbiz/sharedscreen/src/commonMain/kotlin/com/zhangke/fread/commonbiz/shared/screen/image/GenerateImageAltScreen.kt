package com.zhangke.fread.commonbiz.shared.screen.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource

class GenerateImageAltScreen(private val imageUri: String) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<GenerateImageAltViewModel, GenerateImageAltViewModel.Factory> {
            it.create(imageUri)
        }
        val uiState by viewModel.uiState.collectAsState()
        GenerateImageAltScreenContent(
            uiState = uiState,
            onBackClick = { navigator.pop() },
        )
    }

    @Composable
    private fun GenerateImageAltScreenContent(
        uiState: GenerateImageAltUiState,
        onBackClick: () -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(LocalizedString.post_status_image_generate_alt),
                    onBackClick = onBackClick,
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding),
            ) {
                Card(
                    modifier = Modifier.padding(top = 24.dp)
                        .fillMaxWidth()
                        .aspectRatio(1.7F),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        AutoSizeImage(
                            url = uiState.imageUri,
                            modifier = Modifier.align(Alignment.Center),
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}
