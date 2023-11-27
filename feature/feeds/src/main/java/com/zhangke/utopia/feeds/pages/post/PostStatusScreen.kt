package com.zhangke.utopia.feeds.pages.post

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
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
import java.util.Locale

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
                onLanguageSelected = viewModel::onLanguageSelected,
                onDeleteClick = viewModel::onMediaDeleteClick,
                onCancelUploadClick = viewModel::onCancelUploadClick,
                onRetryClick = viewModel::onRetryClick,
                onDescriptionInputted = viewModel::onDescriptionInputted,
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
        onDeleteClick: (PostStatusFile) -> Unit,
        onCancelUploadClick: (PostStatusFile) -> Unit,
        onRetryClick: (PostStatusFile) -> Unit,
        onDescriptionInputted: (PostStatusFile, String) -> Unit,
        onLanguageSelected: (Locale) -> Unit,
    ) {
        val bottomBarHeight = 48.dp
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
                    height = bottomBarHeight,
                    uiState = uiState,
                    onSensitiveClick = onSensitiveClick,
                    onMediaSelected = onMediaSelected,
                    onLanguageSelected = onLanguageSelected,
                )
            }
        ) { paddingValues ->
            val layoutDirection = LocalLayoutDirection.current
            ConstraintLayout(
                modifier = Modifier
                    .padding(
                        start = paddingValues.calculateStartPadding(layoutDirection),
                        top = paddingValues.calculateTopPadding(),
                        end = paddingValues.calculateEndPadding(layoutDirection),
                        bottom = bottomBarHeight,
                    )
                    .navigationBarsPadding()
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val (avatarRef, nameRef, platformRef, switchAccountRef, inputRef, statusAttachmentRef) = createRefs()
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .constrainAs(avatarRef) {
                            start.linkTo(parent.start, 16.dp)
                            top.linkTo(parent.top)
                            width = Dimension.value(36.dp)
                            height = Dimension.value(36.dp)
                        },
                    model = uiState.account.avatar,
                    contentDescription = null,
                )
                Text(
                    modifier = Modifier.constrainAs(nameRef) {
                        start.linkTo(avatarRef.end, 8.dp)
                        top.linkTo(avatarRef.top)
                        end.linkTo(switchAccountRef.start, 6.dp)
                        width = Dimension.fillToConstraints
                    },
                    text = uiState.account.userName,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                )
                Text(
                    modifier = Modifier.constrainAs(platformRef) {
                        start.linkTo(nameRef.start)
                        end.linkTo(switchAccountRef.start, 6.dp)
                        top.linkTo(nameRef.bottom, 2.dp)
                        width = Dimension.fillToConstraints
                    },
                    text = uiState.account.webFinger.toString(),
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
                            imageVector = Icons.Default.MoreVert,
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
                    modifier = Modifier
                        .constrainAs(inputRef) {
                            top.linkTo(platformRef.bottom)
                            start.linkTo(parent.start, 8.dp)
                            end.linkTo(parent.end, 8.dp)
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent
                        },
                    shape = GenericShape { _, _ -> },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.post_screen_input_hint),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    value = uiState.content,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = onContentChanged,
                )
                StatusAttachment(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .constrainAs(statusAttachmentRef) {
                            top.linkTo(inputRef.bottom, 16.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    uiState = uiState,
                    onDeleteClick = onDeleteClick,
                    onCancelUploadClick = onCancelUploadClick,
                    onRetryClick = onRetryClick,
                    onDescriptionInputted = onDescriptionInputted,
                )
            }
        }
    }

    @Composable
    private fun StatusAttachment(
        modifier: Modifier,
        uiState: PostStatusUiState,
        onDeleteClick: (PostStatusFile) -> Unit,
        onCancelUploadClick: (PostStatusFile) -> Unit,
        onRetryClick: (PostStatusFile) -> Unit,
        onDescriptionInputted: (PostStatusFile, String) -> Unit,
    ) {
        val attachment = uiState.attachment ?: return
        when(attachment){
            is PostStatusAttachment.ImageAttachment -> {
                PostStatusImageAttachment(
                    modifier = modifier,
                    attachment = attachment,
                    onDeleteClick = onDeleteClick,
                    onCancelUploadClick = onCancelUploadClick,
                    onRetryClick = onRetryClick,
                    onDescriptionInputted = onDescriptionInputted,
                )
            }
            is PostStatusAttachment.VideoAttachment -> {

            }
        }
    }
}
