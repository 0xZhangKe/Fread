package com.zhangke.fread.bluesky.internal.screen.user.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.collapsable.ScrollUpTopBarLayout
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.bluesky.Res
import com.zhangke.fread.bluesky.bsky_user_detail_action_block_user
import com.zhangke.fread.bluesky.bsky_user_detail_action_block_user_dialog_message
import com.zhangke.fread.bluesky.bsky_user_detail_action_blocked_list
import com.zhangke.fread.bluesky.bsky_user_detail_action_mute_user
import com.zhangke.fread.bluesky.bsky_user_detail_action_mute_user_dialog_message
import com.zhangke.fread.bluesky.bsky_user_detail_action_muted_list
import com.zhangke.fread.bluesky.bsky_user_detail_action_unmute_user
import com.zhangke.fread.bluesky.internal.composable.DetailTopBar
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsTab
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListScreen
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListType
import com.zhangke.fread.bluesky.internal.tracking.BskyTrackingElements
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.handler.LocalActivityTextHandler
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.action.DropDownCopyLinkItem
import com.zhangke.fread.status.ui.action.DropDownOpenInBrowserItem
import com.zhangke.fread.status.ui.action.ModalDropdownMenuItem
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NestedTabConnection
import com.zhangke.fread.status.ui.common.ProgressedAvatar
import com.zhangke.fread.status.ui.common.ProgressedBanner
import com.zhangke.fread.status.ui.common.RelationshipStateButton
import com.zhangke.fread.status.ui.common.RelationshipUiState
import com.zhangke.fread.status.ui.common.UserFollowLine
import com.zhangke.fread.statusui.status_ui_user_detail_follows_you
import org.jetbrains.compose.resources.stringResource

class BskyUserDetailScreen(
    private val role: IdentityRole,
    private val did: String,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val transparentNavigator = LocalTransparentNavigator.current
        val browserLauncher = LocalActivityBrowserLauncher.current
        val activityTextHandler = LocalActivityTextHandler.current
        val viewModel = getViewModel<BskyUserDetailViewModel, BskyUserDetailViewModel.Factory> {
            it.create(role, did)
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarState = rememberSnackbarHostState()
        UserDetailContent(
            uiState = uiState,
            snackbarHostState = snackBarState,
            onBackClick = navigator::pop,
            onBannerClick = { openFullImageScreen(transparentNavigator, uiState.banner) },
            onAvatarClick = { openFullImageScreen(transparentNavigator, uiState.avatar) },
            onFollowClick = viewModel::onFollowClick,
            onBlockClick = viewModel::onBlockClick,
            onUnblockClick = viewModel::onUnblockClick,
            onUnfollowClick = viewModel::onUnfollowClick,
            onFollowerClick = {
                navigator.push(UserListScreen(role = role, type = UserListType.FOLLOWERS))
            },
            onFollowingClick = {
                navigator.push(UserListScreen(role = role, type = UserListType.FOLLOWING))
            },
            onOpenInBrowserClick = {
                uiState.userHomePageUrl?.let { browserLauncher.launchWebTabInApp(it) }
            },
            onCopyLinkClick = {
                uiState.userHomePageUrl?.let { activityTextHandler.copyText(it) }
            },
            onEditProfileClick = {},
            onMuteClick = { viewModel.onMuteClick(true) },
            onUnmuteClick = { viewModel.onMuteClick(false) },
            onBlockedUserListClick = {
                navigator.push(UserListScreen(role = role, type = UserListType.BLOCKED))
            },
            onMuteUserListClick = {
                navigator.push(UserListScreen(role = role, type = UserListType.MUTED))
            },
        )
        ConsumeSnackbarFlow(snackBarState, viewModel.snackBarMessage)
    }

    @Composable
    private fun UserDetailContent(
        uiState: BskyUserDetailUiState,
        snackbarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onBannerClick: () -> Unit,
        onAvatarClick: () -> Unit,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
        onUnblockClick: () -> Unit,
        onFollowerClick: () -> Unit,
        onFollowingClick: () -> Unit,
        onBlockClick: () -> Unit,
        onMuteClick: () -> Unit,
        onUnmuteClick: () -> Unit,
        onOpenInBrowserClick: () -> Unit,
        onCopyLinkClick: () -> Unit,
        onEditProfileClick: () -> Unit,
        onBlockedUserListClick: () -> Unit,
        onMuteUserListClick: () -> Unit,
    ) {
        val contentCanScrollBackward = remember {
            mutableStateOf(false)
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { innerPaddings ->
            ScrollUpTopBarLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings),
                topBarContent = { progress ->
                    DetailTopBar(
                        progress = progress,
                        title = uiState.displayName.orEmpty(),
                        onBackClick = onBackClick,
                        actions = {
                            TopBarActions(
                                uiState = uiState,
                                onBlockClick = onBlockClick,
                                onMuteClick = onMuteClick,
                                onUnmuteClick = onUnmuteClick,
                                onOpenInBrowserClick = onOpenInBrowserClick,
                                onCopyLinkClick = onCopyLinkClick,
                                onEditProfileClick = onEditProfileClick,
                                onBlockedUserListClick = onBlockedUserListClick,
                                onMuteUserListClick = onMuteUserListClick,
                            )
                        },
                    )
                },
                headerContent = { progress ->
                    UserDetailInfo(
                        uiState = uiState,
                        progress = progress,
                        onBannerClick = onBannerClick,
                        onAvatarClick = onAvatarClick,
                        onFollowClick = onFollowClick,
                        onUnfollowClick = onUnfollowClick,
                        onUnblockClick = onUnblockClick,
                        onFollowerClick = onFollowerClick,
                        onFollowingClick = onFollowingClick,
                    )
                },
                contentCanScrollBackward = contentCanScrollBackward,
            ) {
                val tabs = remember(uiState.tabs) {
                    uiState.tabs.map {
                        HomeFeedsTab(
                            contentCanScrollBackward = contentCanScrollBackward,
                            role = role,
                            feeds = it,
                        )
                    }
                }
                val nestedTabConnection = remember {
                    NestedTabConnection()
                }
                CompositionLocalProvider(
                    LocalSnackbarHostState provides snackbarHostState,
                    LocalNestedTabConnection provides nestedTabConnection,
                ) {
                    val contentScrollInProgress by nestedTabConnection.contentScrollInpProgress.collectAsState()
                    HorizontalPagerWithTab(
                        tabList = tabs,
                        pagerUserScrollEnabled = !contentScrollInProgress,
                    )
                }
            }
        }
    }
}

@Composable
private fun UserDetailInfo(
    uiState: BskyUserDetailUiState,
    progress: Float,
    onBannerClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onFollowClick: () -> Unit,
    onUnfollowClick: () -> Unit,
    onUnblockClick: () -> Unit,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit,
) {
    SelectionContainer {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val (bannerRef, avatarRef, nameRef, handleRef, relationRef) = createRefs()
            val (desRef, followRef) = createRefs()
            ProgressedBanner(
                modifier = Modifier
                    .clickable { onBannerClick() }
                    .constrainAs(bannerRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    },
                url = uiState.banner,
            )

            // avatar
            ProgressedAvatar(
                modifier = Modifier
                    .constrainAs(avatarRef) {
                        start.linkTo(parent.start, 16.dp)
                        top.linkTo(bannerRef.bottom)
                        bottom.linkTo(bannerRef.bottom)
                    },
                avatar = uiState.avatar,
                progress = progress,
                loading = uiState.loading,
                onAvatarClick = onAvatarClick,
            )

            // relationship button
            RelationshipStateButton(
                modifier = Modifier.constrainAs(relationRef) {
                    top.linkTo(bannerRef.bottom, 8.dp)
                    end.linkTo(parent.end, 16.dp)
                },
                relationship = uiState.relationship,
                onFollowClick = onFollowClick,
                onUnfollowClick = onUnfollowClick,
                onUnblockClick = onUnblockClick,
            )

            // title
            Text(
                modifier = Modifier
                    .freadPlaceholder(uiState.loading)
                    .constrainAs(nameRef) {
                        top.linkTo(avatarRef.bottom, 16.dp)
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    },
                text = uiState.displayName.orEmpty(),
                maxLines = 1,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
            )

            // subtitle
            DetailSubtitle(
                modifier = Modifier
                    .widthIn(min = 48.dp)
                    .freadPlaceholder(uiState.loading)
                    .constrainAs(handleRef) {
                        top.linkTo(nameRef.bottom, 6.dp)
                        start.linkTo(nameRef.start)
                        width = Dimension.wrapContent
                    },
                uiState = uiState,
            )

            // description
            Text(
                modifier = Modifier
                    .freadPlaceholder(uiState.loading)
                    .fillMaxWidth()
                    .constrainAs(desRef) {
                        top.linkTo(handleRef.bottom, 6.dp)
                        start.linkTo(nameRef.start)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    },
                text = uiState.description.orEmpty(),
            )

            // follow info line
            UserFollowLine(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .freadPlaceholder(uiState.loading)
                    .constrainAs(followRef) {
                        top.linkTo(desRef.bottom)
                        start.linkTo(desRef.start)
                        width = Dimension.wrapContent
                    },
                followersCount = uiState.followersCount,
                followingCount = uiState.followsCount,
                statusesCount = uiState.postsCount,
                onFollowerClick = onFollowerClick,
                onFollowingClick = onFollowingClick,
            )
        }
    }
}

@Composable
private fun DetailSubtitle(
    uiState: BskyUserDetailUiState,
    modifier: Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier,
            text = if (uiState.handle.isNullOrEmpty()) {
                ""
            } else {
                "@${uiState.handle}"
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelMedium,
        )
        if (uiState.relationship == RelationshipUiState.FOLLOWED_BY) {
            Text(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = RoundedCornerShape(2.dp),
                    )
                    .padding(horizontal = 4.dp),
                text = stringResource(com.zhangke.fread.statusui.Res.string.status_ui_user_detail_follows_you),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun TopBarActions(
    uiState: BskyUserDetailUiState,
    onBlockClick: () -> Unit,
    onMuteClick: () -> Unit,
    onUnmuteClick: () -> Unit,
    onOpenInBrowserClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onBlockedUserListClick: () -> Unit,
    onMuteUserListClick: () -> Unit,
) {
    if (uiState.isOwner) {
        SimpleIconButton(
            onClick = onEditProfileClick,
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Profile",
        )
        Box(modifier = Modifier.width(8.dp))
    }
    var showMorePopup by remember {
        mutableStateOf(false)
    }
    SimpleIconButton(
        onClick = { showMorePopup = true },
        imageVector = Icons.Default.MoreVert,
        contentDescription = "More Options"
    )
    var showBlockUserConfirmDialog by remember {
        mutableStateOf(false)
    }
    var showMuteDialog by remember {
        mutableStateOf(false)
    }
    DropdownMenu(
        expanded = showMorePopup,
        onDismissRequest = { showMorePopup = false },
    ) {
        DropDownOpenInBrowserItem {
            reportClick(BskyTrackingElements.USER_DETAIL_OPEN_IN_BROWSER)
            showMorePopup = false
            onOpenInBrowserClick()
        }
        DropDownCopyLinkItem {
            reportClick(BskyTrackingElements.USER_DETAIL_COPY_LINK)
            showMorePopup = false
            onCopyLinkClick()
        }
        if (uiState.isOwner) {
            SelfAccountActions(
                onBlockedUserListClick = onBlockedUserListClick,
                onMuteUserListClick = onMuteUserListClick,
            )
        } else {
            OtherAccountActions(
                uiState = uiState,
                onUnmuteClick = onUnmuteClick,
                onShowMuteDialogClick = { showMuteDialog = true },
                onShowBlockUserConfirmDialog = { showBlockUserConfirmDialog = true },
                onDismissMorePopupRequest = { showMorePopup = false },
            )
        }
    }
    if (showBlockUserConfirmDialog) {
        AlertConfirmDialog(
            content = stringResource(Res.string.bsky_user_detail_action_block_user_dialog_message),
            onConfirm = {
                showBlockUserConfirmDialog = false
                onBlockClick()
            },
            onDismissRequest = { showBlockUserConfirmDialog = false },
        )
    }
    if (showMuteDialog) {
        AlertConfirmDialog(
            content = stringResource(Res.string.bsky_user_detail_action_mute_user_dialog_message),
            onConfirm = {
                showMuteDialog = false
                onMuteClick()
            },
            onDismissRequest = { showMuteDialog = false },
        )
    }
}

@Composable
private fun SelfAccountActions(
    onBlockedUserListClick: () -> Unit,
    onMuteUserListClick: () -> Unit,
) {
    ModalDropdownMenuItem(
        text = stringResource(Res.string.bsky_user_detail_action_muted_list),
        imageVector = Icons.AutoMirrored.Filled.VolumeOff,
        onClick = onMuteUserListClick,
    )
    ModalDropdownMenuItem(
        text = stringResource(Res.string.bsky_user_detail_action_blocked_list),
        imageVector = Icons.Default.Block,
        onClick = onBlockedUserListClick,
    )
}

@Composable
private fun OtherAccountActions(
    uiState: BskyUserDetailUiState,
    onUnmuteClick: () -> Unit,
    onShowMuteDialogClick: () -> Unit,
    onShowBlockUserConfirmDialog: () -> Unit,
    onDismissMorePopupRequest: () -> Unit,
) {
    val fixedName = uiState.displayName?.take(10).orEmpty()
    val muteOrUnmuteText = if (uiState.muted) {
        stringResource(Res.string.bsky_user_detail_action_unmute_user)
    } else {
        stringResource(Res.string.bsky_user_detail_action_mute_user, fixedName)
    }
    ModalDropdownMenuItem(
        text = muteOrUnmuteText,
        imageVector = Icons.AutoMirrored.Filled.VolumeOff,
        onClick = {
            onDismissMorePopupRequest()
            if (uiState.muted) {
                onUnmuteClick()
            } else {
                onShowMuteDialogClick()
            }
        }
    )
    if (!uiState.blocked) {
        ModalDropdownMenuItem(
            text = stringResource(Res.string.bsky_user_detail_action_block_user, fixedName),
            imageVector = Icons.Default.Block,
            onClick = {
                onDismissMorePopupRequest()
                onShowBlockUserConfirmDialog()
            },
        )
    }
}

private fun openFullImageScreen(navigator: TransparentNavigator, url: String?) {
    if (url.isNullOrEmpty()) return
    val screen = ImageViewerScreen(
        selectedIndex = 0,
        imageList = listOf(ImageViewerScreen.Image(url = url)),
    )
    navigator.push(screen)
}
