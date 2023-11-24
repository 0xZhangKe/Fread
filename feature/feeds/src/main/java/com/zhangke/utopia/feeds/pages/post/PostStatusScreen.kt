package com.zhangke.utopia.feeds.pages.post

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiFlags
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.status.account.LoggedAccount

class PostStatusScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<PostStatusViewModel>()
        val loadableUiState by viewModel.uiState.collectAsState()
        LoadableLayout(
            state = loadableUiState,
        ) { uiState ->
            PostStatusScreenContent(
                uiState = uiState,
                onSwitchAccount = {},
                onContentChanged = viewModel::onContentChanged,
                onCloseClick = navigator::pop,
                onPostClick = viewModel::onPostClick,
                onSensitiveClick = viewModel::onSensitiveClick,
                onMediaSelected = viewModel::onMediaSelected,
                onDelete = viewModel::onMediaDeleteClick,
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PostStatusScreenContent(
        uiState: PostStatusUiState,
        onSwitchAccount: (LoggedAccount) -> Unit,
        onContentChanged: (String) -> Unit,
        onCloseClick: () -> Unit,
        onPostClick: () -> Unit,
        onSensitiveClick: () -> Unit,
        onMediaSelected: (List<Uri>) -> Unit,
        onDelete: (Uri) -> Unit,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        SimpleIconButton(
                            onClick = onCloseClick,
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                        )
                    },
                    actions = {
                        SimpleIconButton(
                            onClick = onPostClick,
                            imageVector = Icons.Default.Send,
                            contentDescription = "Post",
                        )
                    },
                    title = {
                        Text(
                            text = stringResource(R.string.post_status_page_title),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                )
            },
            bottomBar = {
                PostStatusBottomBar(
                    uiState = uiState,
                    onSensitiveClick = onSensitiveClick,
                    onMediaSelected = onMediaSelected,
                )
            }
        ) { paddingValues ->
            ConstraintLayout(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                val (avatarRef, nameRef, platformRef, switchAccountRef, inputRef, mediaRef) = createRefs()
                AsyncImage(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .constrainAs(avatarRef) {
                            start.linkTo(parent.start, 16.dp)
                            top.linkTo(parent.top, 16.dp)
                            width = Dimension.value(42.dp)
                            width = Dimension.value(42.dp)
                        },
                    model = uiState.account.active,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.constrainAs(nameRef) {
                        start.linkTo(avatarRef.end, 8.dp)
                        top.linkTo(avatarRef.top)
                        end.linkTo(switchAccountRef.start)
                    },
                    text = uiState.account.userName,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                )
                Text(
                    modifier = Modifier.constrainAs(platformRef) {
                        start.linkTo(nameRef.start)
                        top.linkTo(nameRef.bottom, 2.dp)
                    },
                    text = uiState.account.uri.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Box(
                    modifier = Modifier.constrainAs(switchAccountRef) {
                        end.linkTo(parent.end, 16.dp)
                        top.linkTo(nameRef.top)
                    }
                ) {
                    val availableAccountList = uiState.availableAccountList
                    if (availableAccountList.size > 1) {
                        var showAccountSwitchPopup by remember {
                            mutableStateOf(false)
                        }
                        SimpleIconButton(
                            onClick = { showAccountSwitchPopup = true },
                            imageVector = Icons.Default.SwitchAccount,
                            contentDescription = "Switch Account",
                        )
                        DropdownMenu(
                            expanded = showAccountSwitchPopup,
                            onDismissRequest = { showAccountSwitchPopup = false },
                        ) {
                            availableAccountList.forEach {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "${it.userName}@${it.platform.name}",
                                        )
                                    },
                                    onClick = { onSwitchAccount(it) },
                                )
                            }
                        }
                    }
                }
                TextField(
                    modifier = Modifier.constrainAs(inputRef) {
                        top.linkTo(avatarRef.bottom, 16.dp)
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                        bottom.linkTo(parent.bottom, 16.dp)
                    },
                    value = uiState.content,
                    onValueChange = onContentChanged,
                )
                StatusMedias(
                    modifier = Modifier.constrainAs(mediaRef) {

                    },
                    uiState = uiState,
                    onDelete = onDelete,
                )
            }
        }
    }

    @Composable
    private fun PostStatusBottomBar(
        uiState: PostStatusUiState,
        onSensitiveClick: () -> Unit,
        onMediaSelected: (List<Uri>) -> Unit,
    ) {
        Row {
            SelectedMediaIconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp),
                onMediaSelected = onMediaSelected,
                allowedSelectCount = uiState.allowedSelectCount,
            )
            SimpleIconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp),
                onClick = { /*TODO*/ },
                imageVector = Icons.Default.Poll,
                contentDescription = "Add Poll",
            )
            SimpleIconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp),
                onClick = { /*TODO*/ },
                imageVector = Icons.Default.EmojiFlags,
                contentDescription = "Add Emoji",
            )
            SimpleIconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp),
                onClick = onSensitiveClick,
                imageVector = Icons.Default.AddAlert,
                contentDescription = "Sensitive content",
            )
            SimpleIconButton(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 16.dp),
                onClick = { /*TODO*/ },
                imageVector = Icons.Default.Language,
                contentDescription = "Choose language",
            )
            Spacer(modifier = Modifier.weight(1F))
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp),
                text = "1000",
            )
        }
    }

    @Composable
    private fun SelectedMediaIconButton(
        modifier: Modifier,
        allowedSelectCount: Int,
        onMediaSelected: (List<Uri>) -> Unit,
    ) {
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(maxItems = allowedSelectCount), onMediaSelected)
        SimpleIconButton(
            modifier = modifier,
            onClick = {
                launcher.launch(
                    PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                        .build()
                )
            },
            imageVector = Icons.Default.Image,
            contentDescription = "Add Image",
        )
    }

    @Composable
    private fun StatusMedias(
        modifier: Modifier,
        uiState: PostStatusUiState,
        onDelete: (Uri) -> Unit,
    ) {
    }
}
