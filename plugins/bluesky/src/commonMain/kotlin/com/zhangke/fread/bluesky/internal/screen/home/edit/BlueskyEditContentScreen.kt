package com.zhangke.fread.bluesky.internal.screen.home.edit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.successDataOrNull
import com.zhangke.fread.bluesky.Res
import com.zhangke.fread.bluesky.bsky_edit_content_title
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.ui.bar.EditContentTopBar
import org.jetbrains.compose.resources.stringResource

class BlueskyEditContentScreen(
    private val contentId: String,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val snackBarHostState = rememberSnackbarHostState()
        val viewModel =
            getViewModel<BlueskyEditContentViewModel, BlueskyEditContentViewModel.Factory> {
                it.create(contentId)
            }
        val uiState by viewModel.uiState.collectAsState()

    }

    @Composable
    private fun BlueskyEditContent(
        loadableUiState: LoadableState<BlueskyEditContentUiState>,
        onBackClick: () -> Unit,
        onNameEdit: (String) -> Unit,
        onDeleteClick: () -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.bsky_edit_content_title),
                    onBackClick = onBackClick,
                    actions = {
                        val uiState = loadableUiState.successDataOrNull()
                        if (uiState != null) {
                            EditContentTopBar(
                                contentName = uiState.content.name,
                                onBackClick = onBackClick,
                                onNameEdit = onNameEdit,
                                onDeleteClick = onDeleteClick,
                            )
                        }
                    },
                )
            }
        ) { innerPadding ->
            LoadableLayout(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                state = loadableUiState,
            ) { uiState ->

            }
        }
    }
}
