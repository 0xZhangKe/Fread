package com.zhangke.fread.bluesky.internal.screen.user.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.screen.feeds.following.BskyFollowingFeedsPage
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsScreen
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsTab
import com.zhangke.fread.bluesky.internal.screen.search.SearchStatusScreen
import com.zhangke.fread.bluesky.internal.screen.user.edit.EditProfileScreen
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListScreen
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListType
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.browser.launchWebTabInApp
import com.zhangke.fread.common.handler.LocalActivityTextHandler
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.fread.status.ui.action.DropDownCopyLinkItem
import com.zhangke.fread.status.ui.action.DropDownOpenInBrowserItem
import com.zhangke.fread.status.ui.action.ModalDropdownMenuItem
import com.zhangke.fread.status.ui.common.DetailPageScaffold
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NestedTabConnection
import com.zhangke.fread.status.ui.common.RelationshipStateButton
import com.zhangke.fread.status.ui.common.UserFollowLine
import com.zhangke.fread.status.ui.user.UserHandleLine
import org.jetbrains.compose.resources.stringResource

class BskyUserDetailScreen(
    private val locator: PlatformLocator,
    private val did: String,
) : BaseScreen() {

    override val key: ScreenKey
        get() = locator.toString() + did

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val transparentNavigator = LocalTransparentNavigator.current
        val browserLauncher = LocalActivityBrowserLauncher.current
        val activityTextHandler = LocalActivityTextHandler.current
        val viewModel = getViewModel<BskyUserDetailViewModel, BskyUserDetailViewModel.Factory> {
            it.create(locator, did)
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarState = rememberSnackbarHostState()
        val coroutineScope = rememberCoroutineScope()
        UserDetailContent(
            uiState = uiState,
            snackbarHostState = snackBarState,
            onBackClick = navigator::pop,
            onSearchClick = {
                navigator.push(SearchStatusScreen(locator = locator, did = did))
            },
            onBannerClick = { openFullImageScreen(transparentNavigator, uiState.banner) },
            onAvatarClick = { openFullImageScreen(transparentNavigator, uiState.avatar) },
            onFollowClick = viewModel::onFollowClick,
            onBlockClick = viewModel::onBlockClick,
            onUnblockClick = viewModel::onUnblockClick,
            onUnfollowClick = viewModel::onUnfollowClick,
            onFollowerClick = {
                navigator.push(UserListScreen(locator = locator, type = UserListType.FOLLOWERS))
            },
            onFollowingClick = {
                navigator.push(UserListScreen(locator = locator, type = UserListType.FOLLOWING))
            },
            onOpenInBrowserClick = {
                uiState.userHomePageUrl?.let { browserLauncher.launchWebTabInApp(coroutineScope, it) }
            },
            onCopyLinkClick = {
                uiState.userHomePageUrl?.let { activityTextHandler.copyText(it) }
            },
            onEditProfileClick = { navigator.push(EditProfileScreen(locator = locator)) },
            onMuteClick = { viewModel.onMuteClick(true) },
            onUnmuteClick = { viewModel.onMuteClick(false) },
            onBlockedUserListClick = {
                navigator.push(UserListScreen(locator = locator, type = UserListType.BLOCKED))
            },
            onMuteUserListClick = {
                navigator.push(UserListScreen(locator = locator, type = UserListType.MUTED))
            },
            onHashtagClick = { tag ->
                HomeFeedsScreen.create(
                    feeds = BlueskyFeeds.Hashtags(tag),
                    locator = locator,
                ).let { navigator.push(it) }
            },
            onFollowingFeedsClick = {
                navigator.push(BskyFollowingFeedsPage(contentId = null, locator = locator))
            },
            onLogoutClick = viewModel::onLogoutClick,
        )
        ConsumeSnackbarFlow(snackBarState, viewModel.snackBarMessage)
        LaunchedEffect(Unit) { viewModel.onPageResume() }
        ConsumeFlow(viewModel.finishPageFlow) { navigator.pop() }
    }

    @Composable
    private fun UserDetailContent(
        uiState: BskyUserDetailUiState,
        snackbarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onSearchClick: () -> Unit,
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
        onHashtagClick: (String) -> Unit,
        onFollowingFeedsClick: () -> Unit,
        onLogoutClick: () -> Unit,
    ) {
        val contentCanScrollBackward = remember { mutableStateOf(false) }
        DetailPageScaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHostState = snackbarHostState,
            title = remember(uiState.displayName) {
                buildRichText(uiState.displayName.orEmpty())
            },
            avatar = uiState.avatar.orEmpty(),
            banner = uiState.banner,
            description = remember(uiState.description) {
                buildRichText(uiState.description.orEmpty())
            },
            privateNote = null,
            loading = uiState.loading,
            contentCanScrollBackward = contentCanScrollBackward,
            onBannerClick = onBannerClick,
            onAvatarClick = onAvatarClick,
            onUrlClick = {},
            onMaybeHashtagClick = onHashtagClick,
            onBackClick = onBackClick,
            topBarActions = {
                TopBarActions(
                    uiState = uiState,
                    onBlockClick = onBlockClick,
                    onMuteClick = onMuteClick,
                    onSearchClick = onSearchClick,
                    onUnmuteClick = onUnmuteClick,
                    onOpenInBrowserClick = onOpenInBrowserClick,
                    onCopyLinkClick = onCopyLinkClick,
                    onFollowingFeedsClick = onFollowingFeedsClick,
                    onBlockedUserListClick = onBlockedUserListClick,
                    onMuteUserListClick = onMuteUserListClick,
                    onLogoutClick = onLogoutClick,
                )
            },
            handleLine = {
                UserHandleLine(
                    modifier = Modifier,
                    handle = uiState.prettyHandle,
                    bot = false,
                    followedBy = uiState.relationship?.followedBy == true,
                )
            },
            followInfoLine = {
                UserFollowLine(
                    modifier = Modifier,
                    followersCount = uiState.followersCount,
                    followingCount = uiState.followsCount,
                    statusesCount = uiState.postsCount,
                    onFollowerClick = onFollowerClick,
                    onFollowingClick = onFollowingClick,
                )
            },
            topDetailContentAction = {
                if (uiState.isOwner) {
                    FilledTonalButton(
                        onClick = onEditProfileClick,
                    ) {
                        Text(
                            text = stringResource(LocalizedString.statusUiEditProfile)
                        )
                    }
                } else if (uiState.relationship != null) {
                    RelationshipStateButton(
                        modifier = Modifier,
                        relationship = uiState.relationship,
                        onFollowClick = onFollowClick,
                        onUnfollowClick = onUnfollowClick,
                        onUnblockClick = onUnblockClick,
                        onCancelFollowRequestClick = {},
                    )
                }
            },
        ) {
            val tabs = remember(uiState.tabs) {
                uiState.tabs.map {
                    HomeFeedsTab(
                        contentCanScrollBackward = contentCanScrollBackward,
                        locator = locator,
                        feeds = it,
                    )
                }
            }
            val nestedTabConnection = remember { NestedTabConnection() }
            CompositionLocalProvider(
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

@Composable
private fun TopBarActions(
    uiState: BskyUserDetailUiState,
    onSearchClick: () -> Unit,
    onBlockClick: () -> Unit,
    onMuteClick: () -> Unit,
    onUnmuteClick: () -> Unit,
    onOpenInBrowserClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onBlockedUserListClick: () -> Unit,
    onMuteUserListClick: () -> Unit,
    onFollowingFeedsClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    SimpleIconButton(
        onClick = onSearchClick,
        imageVector = Icons.Default.Search,
        contentDescription = stringResource(LocalizedString.search),
    )
    if (uiState.isOwner) {
        SimpleIconButton(
            onClick = onFollowingFeedsClick,
            imageVector = Icons.AutoMirrored.Outlined.ListAlt,
            contentDescription = stringResource(LocalizedString.feeds),
        )
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
            showMorePopup = false
            onOpenInBrowserClick()
        }
        DropDownCopyLinkItem {
            showMorePopup = false
            onCopyLinkClick()
        }
        if (uiState.isOwner) {
            SelfAccountActions(
                onBlockedUserListClick = onBlockedUserListClick,
                onMuteUserListClick = onMuteUserListClick,
                onLogoutClick = onLogoutClick,
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
            content = stringResource(LocalizedString.bsky_user_detail_action_block_user_dialog_message),
            onConfirm = {
                showBlockUserConfirmDialog = false
                onBlockClick()
            },
            onDismissRequest = { showBlockUserConfirmDialog = false },
        )
    }
    if (showMuteDialog) {
        AlertConfirmDialog(
            content = stringResource(LocalizedString.bsky_user_detail_action_mute_user_dialog_message),
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
    onLogoutClick: () -> Unit,
) {
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.bsky_user_detail_action_muted_list),
        imageVector = Icons.AutoMirrored.Filled.VolumeOff,
        onClick = onMuteUserListClick,
    )
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.bsky_user_detail_action_blocked_list),
        imageVector = Icons.Default.Block,
        onClick = onBlockedUserListClick,
    )
    var showLogoutDialog by remember { mutableStateOf(false) }
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.statusUiLogout),
        imageVector = Icons.AutoMirrored.Filled.Logout,
        colors = MenuDefaults.itemColors(
            textColor = MaterialTheme.colorScheme.error,
            leadingIconColor = MaterialTheme.colorScheme.error,
        ),
        onClick = { showLogoutDialog = true },
    )
    if (showLogoutDialog) {
        FreadDialog(
            onDismissRequest = { showLogoutDialog = false },
            contentText = stringResource(LocalizedString.statusUiLogoutDialogContent),
            onPositiveClick = {
                showLogoutDialog = false
                onLogoutClick()
            },
            onNegativeClick = { showLogoutDialog = false },
        )
    }
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
        stringResource(LocalizedString.bsky_user_detail_action_unmute_user)
    } else {
        stringResource(LocalizedString.bsky_user_detail_action_mute_user, fixedName)
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
            text = stringResource(LocalizedString.bsky_user_detail_action_block_user, fixedName),
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
