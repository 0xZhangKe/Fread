package com.zhangke.fread.activitypub.app.internal.screen.status.post

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.toast.toast
import com.zhangke.framework.utils.TextFieldUtils
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.model.PostStatusVisibility
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.PostStatusBottomBar
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.PostStatusImageAttachment
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.PostStatusPoll
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.PostStatusVideoAttachment
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.PostStatusVisibilityUi
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.PostStatusWarning
import com.zhangke.fread.activitypub.app.internal.screen.status.post.composable.TwoTextsInRow
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import java.util.Locale
import kotlin.time.Duration
import com.zhangke.fread.statusui.R as StatusUiR

@Destination(PostStatusScreenRoute.ROUTE)
class PostStatusScreen(
    @Router private val route: String = "",
) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<PostStatusViewModel, PostStatusViewModel.Factory> {
            it.create(PostStatusScreenRoute.parse(route))
        }
        val loadableUiState by viewModel.uiState.collectAsState()
        val postStatus by viewModel.postState.collectAsState(initial = LoadableState.idle())
        var showExitDialog by remember {
            mutableStateOf(false)
        }

        val snackMessageState = rememberSnackbarHostState()

        fun onBack() {
            if (loadableUiState !is LoadableState.Success) {
                navigator.pop()
                return
            }
            if (loadableUiState.requireSuccessData().hasInputtedData()) {
                showExitDialog = true
                return
            }
            navigator.pop()
        }
        LoadableLayout(
            modifier = Modifier.fillMaxSize(),
            state = loadableUiState,
        ) { uiState ->
            PostStatusScreenContent(
                uiState = uiState,
                postStatus = postStatus,
                snackMessageState = snackMessageState,
                onSwitchAccount = viewModel::onSwitchAccountClick,
                onContentChanged = viewModel::onContentChanged,
                onCloseClick = {
                    onBack()
                },
                onPostClick = viewModel::onPostClick,
                onSensitiveClick = viewModel::onSensitiveClick,
                onMediaSelected = viewModel::onMediaSelected,
                onLanguageSelected = viewModel::onLanguageSelected,
                onDeleteClick = viewModel::onMediaDeleteClick,
                onCancelUploadClick = viewModel::onCancelUploadClick,
                onRetryClick = viewModel::onRetryClick,
                onDescriptionInputted = viewModel::onDescriptionInputted,
                onPollClicked = viewModel::onPollClicked,
                onPollContentChanged = viewModel::onPollContentChanged,
                onAddPollItemClick = viewModel::onAddPollItemClick,
                onRemovePollClick = viewModel::onRemovePollClick,
                onRemovePollItemClick = viewModel::onRemovePollItemClick,
                onPollStyleSelect = viewModel::onPollStyleSelect,
                onWarningContentChanged = viewModel::onWarningContentChanged,
                onVisibilityChanged = viewModel::onVisibilityChanged,
                onDurationSelect = viewModel::onDurationSelect,
                onDeleteEmojiClick = viewModel::onEmojiDeleteClick,
            )
        }
        if (postStatus is LoadableState.Success) {
            val successMessage = stringResource(R.string.post_status_success)
            LaunchedEffect(Unit) {
                toast(successMessage)
                navigator.pop()
            }
        }
        BackHandler {
            onBack()
        }
        if (showExitDialog) {
            FreadDialog(
                onDismissRequest = { showExitDialog = false },
                content = {
                    Text(text = stringResource(R.string.post_status_exit_dialog_content))
                },
                onNegativeClick = {
                    showExitDialog = false
                },
                onPositiveClick = {
                    showExitDialog = false
                    navigator.pop()
                },
            )
        }
        ConsumeSnackbarFlow(snackMessageState, viewModel.snackMessage)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PostStatusScreenContent(
        uiState: PostStatusUiState,
        postStatus: LoadableState<Unit>,
        snackMessageState: SnackbarHostState,
        onSwitchAccount: (ActivityPubLoggedAccount) -> Unit,
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
        onPollClicked: () -> Unit,
        onPollContentChanged: (Int, String) -> Unit,
        onRemovePollClick: () -> Unit,
        onRemovePollItemClick: (Int) -> Unit,
        onAddPollItemClick: () -> Unit,
        onPollStyleSelect: (multiple: Boolean) -> Unit,
        onWarningContentChanged: (String) -> Unit,
        onVisibilityChanged: (PostStatusVisibility) -> Unit,
        onDurationSelect: (Duration) -> Unit,
        onDeleteEmojiClick: () -> Unit,
    ) {
        val bottomBarHeight = 48.dp
        if (postStatus is LoadableState.Failed) {
            var errorMessage = stringResource(R.string.post_status_failed)
            if (postStatus.exception.message.isNullOrEmpty().not()) {
                errorMessage += ": ${postStatus.exception.message?.take(180)}"
            }
            LaunchedEffect(errorMessage) {
                snackMessageState.showSnackbar(errorMessage)
            }
        }
        var textFieldValue by remember {
            mutableStateOf(TextFieldValue(uiState.initialContent.orEmpty()))
        }
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackMessageState)
            },
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
                        when (postStatus) {
                            is LoadableState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .size(24.dp)
                                )
                            }

                            else -> {
                                SimpleIconButton(
                                    onClick = onPostClick,
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Post",
                                )
                            }
                        }
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
                    onPollClicked = onPollClicked,
                    onEmojiPick = {
                        textFieldValue = TextFieldUtils.insertText(
                            value = textFieldValue,
                            insertText = " :${it.shortcode}: ",
                        )
                        onContentChanged(textFieldValue.text)
                    },
                    onDeleteEmojiClick = onDeleteEmojiClick,
                )
            }
        ) { paddingValues ->
            ConstraintLayout(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val (
                    replayToBlogRef,
                    avatarRef,
                    nameRef,
                    visibilityRef,
                    switchAccountRef,
                    warningRef,
                    inputRef,
                    statusAttachmentRef,
                ) = createRefs()
                if (uiState.replyToAuthorInfo != null) {
                    Row(
                        modifier = Modifier.constrainAs(replayToBlogRef) {
                            top.linkTo(parent.top, 8.dp)
                            start.linkTo(parent.start, 16.dp)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                            height = Dimension.wrapContent
                        },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Reply,
                            contentDescription = null,
                        )
                        val replyLabel = stringResource(StatusUiR.string.status_ui_reply)
                        Text(
                            modifier = Modifier.padding(start = 4.dp),
                            text = "$replyLabel ${uiState.replyToAuthorInfo.replyAuthorName}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                } else {
                    Box(modifier = Modifier.constrainAs(replayToBlogRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        width = Dimension.value(0.dp)
                        height = Dimension.value(0.dp)
                    })
                }
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .constrainAs(avatarRef) {
                            start.linkTo(parent.start, 8.dp)
                            top.linkTo(replayToBlogRef.bottom, 16.dp)
                            width = Dimension.value(36.dp)
                            height = Dimension.value(36.dp)
                        },
                    model = uiState.account.avatar,
                    contentDescription = null,
                )
                NameAndAccountInfo(
                    modifier = Modifier.constrainAs(nameRef) {
                        start.linkTo(avatarRef.end, 8.dp)
                        top.linkTo(avatarRef.top)
                        end.linkTo(switchAccountRef.start, 2.dp)
                        width = Dimension.fillToConstraints
                    },
                    uiState = uiState,
                )
                PostStatusVisibilityUi(
                    modifier = Modifier
                        .constrainAs(visibilityRef) {
                            top.linkTo(nameRef.bottom, 4.dp)
                            start.linkTo(nameRef.start)
                        },
                    visibility = uiState.visibility,
                    onVisibilitySelect = onVisibilityChanged,
                )
                Box(
                    modifier = Modifier.constrainAs(switchAccountRef) {
                        end.linkTo(parent.end, 4.dp)
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
                                    onClick = {
                                        showAccountSwitchPopup = false
                                        onSwitchAccount(it)
                                    },
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.constrainAs(warningRef) {
                        top.linkTo(visibilityRef.bottom, 8.dp)
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                ) {
                    if (uiState.sensitive) {
                        PostStatusWarning(
                            modifier = Modifier.fillMaxWidth(),
                            warning = uiState.warningContent,
                            onValueChanged = onWarningContentChanged,
                        )
                    }
                }
                TextField(
                    modifier = Modifier
                        .constrainAs(inputRef) {
                            top.linkTo(warningRef.bottom)
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
                    value = textFieldValue,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = {
                        textFieldValue = it
                        onContentChanged(it.text)
                    },
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
                    onPollContentChanged = onPollContentChanged,
                    onRemovePollClick = onRemovePollClick,
                    onRemovePollItemClick = onRemovePollItemClick,
                    onAddPollItemClick = onAddPollItemClick,
                    onPollStyleSelect = onPollStyleSelect,
                    onDurationSelect = onDurationSelect,
                )
            }
        }
    }

    @Composable
    private fun NameAndAccountInfo(
        modifier: Modifier,
        uiState: PostStatusUiState,
    ) {
        TwoTextsInRow(
            firstText = {
                Text(
                    modifier = Modifier,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = uiState.account.userName,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Start,
                )
            },
            secondText = {
                Text(
                    modifier = Modifier,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = uiState.account.webFinger.toString(),
                    style = MaterialTheme.typography.bodySmall,
                )
            },
            spacing = 2.dp,
            modifier = modifier,
        )
    }

    @Composable
    private fun StatusAttachment(
        modifier: Modifier,
        uiState: PostStatusUiState,
        onDeleteClick: (PostStatusFile) -> Unit,
        onCancelUploadClick: (PostStatusFile) -> Unit,
        onRetryClick: (PostStatusFile) -> Unit,
        onDescriptionInputted: (PostStatusFile, String) -> Unit,
        onPollContentChanged: (Int, String) -> Unit,
        onRemovePollClick: () -> Unit,
        onRemovePollItemClick: (Int) -> Unit,
        onAddPollItemClick: () -> Unit,
        onPollStyleSelect: (multiple: Boolean) -> Unit,
        onDurationSelect: (Duration) -> Unit,
    ) {
        val attachment = uiState.attachment ?: return
        when (attachment) {
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
                PostStatusVideoAttachment(
                    modifier = modifier,
                    attachment = attachment,
                    onDeleteClick = onDeleteClick,
                    onCancelUploadClick = onCancelUploadClick,
                    onRetryClick = onRetryClick,
                    onDescriptionInputted = onDescriptionInputted,
                )
            }

            is PostStatusAttachment.Poll -> {
                PostStatusPoll(
                    modifier = modifier,
                    poll = attachment,
                    onPollContentChanged = onPollContentChanged,
                    onRemovePollClick = onRemovePollClick,
                    onRemoveItemClick = onRemovePollItemClick,
                    onAddPollItemClick = onAddPollItemClick,
                    onPollStyleSelect = onPollStyleSelect,
                    onDurationSelect = onDurationSelect,
                )
            }
        }
    }
}
