package com.zhangke.fread.activitypub.app.internal.screen.status.post

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.input_media_desc_input_hint
import com.zhangke.fread.activitypub.app.input_media_desc_page_title
import com.zhangke.fread.common.page.BaseScreen
import org.jetbrains.compose.resources.stringResource
import kotlin.jvm.Transient

class InputMediaDescriptionScreen(
    private val previewUrl: String,
    private val description: String?,
    @Transient private val onDescriptionInputted: (String) -> Unit,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        var inputtedText by remember {
            mutableStateOf(description.orEmpty())
        }
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.input_media_desc_page_title),
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
                AutoSizeImage(
                    previewUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                        .aspectRatio(1F),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .imePadding()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(4.dp),
                        ),
                    value = inputtedText,
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    placeholder = {
                        Text(text = stringResource(Res.string.input_media_desc_input_hint))
                    },
                    onValueChange = {
                        inputtedText = it
                    },
                )
            }
        }
    }
}
