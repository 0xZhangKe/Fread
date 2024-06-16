package com.zhangke.fread.activitypub.app.internal.screen.status.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.activitypub.app.R

class InputMediaDescriptionScreen(
    private val file: PostStatusFile,
    private val onDescriptionInputted: (String) -> Unit,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        var inputtedText by remember {
            mutableStateOf(file.description.orEmpty())
        }
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(R.string.input_media_desc_page_title),
                    onBackClick = {
                        onDescriptionInputted(inputtedText)
                        navigator.pop()
                    },
                    actions = {
                        SimpleIconButton(
                            onClick = {
                                onDescriptionInputted(inputtedText)
                                navigator.pop()
                            },
                            imageVector = Icons.Default.Done,
                            contentDescription = "Save",
                        )
                    }
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .aspectRatio(1F),
                    model = file.file.uri,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .imePadding(),
                    value = inputtedText,
                    placeholder = {
                        Text(text = stringResource(R.string.input_media_desc_input_hint))
                    },
                    onValueChange = {
                        inputtedText = it
                    }
                )
            }

        }
    }
}
