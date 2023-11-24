package com.zhangke.utopia.feeds.pages.post

import android.net.Uri
import android.util.Log
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiFlags
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
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
import com.zhangke.framework.utils.rememberPickVisualMediaLauncher
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.status.account.LoggedAccount

class PostStatusScreen : AndroidScreen() {

    companion object {

        private const val MEDIA_ASPECT = 1.78F
    }

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
        val bottomBarHeight = 40.dp
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
            ) {
                val (avatarRef, nameRef, platformRef, switchAccountRef, inputRef, mediaRef) = createRefs()
                AsyncImage(
                    modifier = Modifier
                        .clip(CircleShape)
                        .constrainAs(avatarRef) {
                            start.linkTo(parent.start, 16.dp)
                            top.linkTo(parent.top, 16.dp)
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
                    modifier = Modifier.constrainAs(inputRef) {
                        top.linkTo(platformRef.bottom)
                        start.linkTo(parent.start, 8.dp)
                        end.linkTo(parent.end, 8.dp)
                        bottom.linkTo(mediaRef.top)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                    shape = GenericShape { _, _ -> },
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
                StatusMedias(
                    modifier = Modifier.constrainAs(mediaRef) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    uiState = uiState,
                    onDelete = onDelete,
                )
            }
        }
    }

    @Composable
    private fun PostStatusBottomBar(
        height: Dp,
        uiState: PostStatusUiState,
        onSensitiveClick: () -> Unit,
        onMediaSelected: (List<Uri>) -> Unit,
    ) {
        val bottomPaddingByIme = WindowInsets.ime
            .asPaddingValues()
            .calculateBottomPadding()
        val modifier = if (bottomPaddingByIme > 0.dp) {
            Modifier.padding(bottom = bottomPaddingByIme)
        } else {
            Modifier.navigationBarsPadding()
        }
        Surface(modifier = modifier.height(height)) {
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
    }

    @Composable
    private fun SelectedMediaIconButton(
        modifier: Modifier,
        allowedSelectCount: Int,
        onMediaSelected: (List<Uri>) -> Unit,
    ) {
        val launcher = if (allowedSelectCount > 0) {
            rememberPickVisualMediaLauncher(
                maxItems = allowedSelectCount,
                onResult = onMediaSelected,
            )
        } else {
            null
        }
        SimpleIconButton(
            modifier = modifier,
            onClick = {
                launcher?.launch(
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
        Box(
            modifier = modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
        ) {
            MediaLayout(
                count = uiState.mediaList.size,
                itemContent = { index ->
                    val mediaUri = uiState.mediaList[index]
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(6.dp)),
                            model = mediaUri,
                            contentScale = ContentScale.Crop,
                            contentDescription = "Media",
                        )
                        SimpleIconButton(
                            modifier = Modifier
                                .align(Alignment.TopEnd),
                            onClick = { onDelete(mediaUri) },
                            imageVector = Icons.Default.Close,
                            tint = Color.White,
                            contentDescription = "Delete",
                        )
                    }
                }
            )
        }
    }

    @Composable
    private fun MediaLayout(
        count: Int,
        itemContent: @Composable (Int) -> Unit,
    ) {
        when (count) {
            1 -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(MEDIA_ASPECT)
                ) {
                    itemContent(0)
                }
            }

            2 -> {
                DoubleMediaSingleRow(itemContent)
            }

            3 -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(MEDIA_ASPECT)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1F)
                            .fillMaxHeight()
                    ) {
                        itemContent(0)
                    }
                    Spacer(
                        modifier = Modifier
                            .height(1.dp)
                            .width(16.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1F)
                            .fillMaxHeight()
                    ) {
                        itemContent(1)
                    }
                    Spacer(
                        modifier = Modifier
                            .height(1.dp)
                            .width(16.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1F)
                            .fillMaxHeight()
                    ) {
                        itemContent(2)
                    }
                }
            }

            4 -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    DoubleMediaSingleRow(itemContent = itemContent)
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(16.dp)
                    )
                    DoubleMediaSingleRow(itemContent = { itemContent(it + 2) })
                }
            }
        }
    }

    @Composable
    private fun DoubleMediaSingleRow(
        itemContent: @Composable (Int) -> Unit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(MEDIA_ASPECT)
        ) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
            ) {
                itemContent(0)
            }
            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .width(16.dp)
            )
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
            ) {
                itemContent(1)
            }
        }
    }
}
