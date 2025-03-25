package com.zhangke.fread.commonbiz.shared.screen.publish

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.publish.composable.InputBlogTextField
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_blog_title
import org.jetbrains.compose.resources.stringResource

class PublishBlogScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = rememberSnackbarHostState()
//        PublishBlogContent(
//            snackbarHostState = snackbarHostState,
//            onBackClick = navigator::pop,
//        )
    }

    @Composable
    private fun PublishBlogContent(
        uiState: PublishBlogUiState,
        onContentChanged: (TextFieldValue) -> Unit,
        snackbarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.shared_publish_blog_title),
                    onBackClick = onBackClick,
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
            ) {
//                InputBlogTextField(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 8.dp),
//                    textFieldValue = uiState.content,
//                    onContentChanged = onContentChanged,
//                )
            }
        }
    }
}
