package com.zhangke.fread.commonbiz.shared.screen.publish

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.localization.LocalizedString
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
object PublishBlogScreenNavKey : NavKey

@Composable
fun PublishBlogScreen() {
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
                title = stringResource(LocalizedString.sharedPublishBlogTitle),
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
