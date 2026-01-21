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
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.HorizontalPagerWithTab
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.screen.feeds.following.BskyFollowingFeedsPageNavKey
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsTab
import com.zhangke.fread.bluesky.internal.screen.search.SearchStatusScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.user.edit.EditProfileScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListType
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.browser.launchWebTabInApp
import com.zhangke.fread.common.handler.LocalTextHandler
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerImage
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreenNavKey
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.fread.status.ui.action.DropDownCopyLinkItem
import com.zhangke.fread.status.ui.action.DropDownOpenInBrowserItem
import com.zhangke.fread.status.ui.action.ModalDropdownMenuItem
import com.zhangke.fread.status.ui.common.DetailPageScaffold
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.LocalStatusSharedElementConfig
import com.zhangke.fread.status.ui.common.NestedTabConnection
import com.zhangke.fread.status.ui.common.RelationshipStateButton
import com.zhangke.fread.status.ui.common.UserFollowLine
import com.zhangke.fread.status.ui.user.UserHandleLine
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class BskyUserDetailScreenNavKey(
    val locator: PlatformLocator,
    val did: String,
) : NavKey

@Composable
fun BskyUserDetailScreen(
    locator: PlatformLocator,
    did: String,
    viewModel: BskyUserDetailViewModel,
) {
    val backStack = LocalNavBackStack.currentOrThrow
    val browserLauncher = LocalActivityBrowserLauncher.current
    val activityTextHandler = LocalTextHandler.current
    val uiState by viewModel.uiState.collectAsState()
    val snackBarState = rememberSnackbarHostState()
    val coroutineScope = rememberCoroutineScope()
    UserDetailContent(
        uiState = uiState,
        snackbarHostState = snackBarState,
        locator = locator,
        onBackClick = backStack::removeLastOrNull,
        onSearchClick = {
            backStack.add(SearchStatusScreenNavKey(locator = locator, did = did))
        },
        onBannerClick = { openFullImageScreen(backStack, uiState.banner) },
        onAvatarClick = { openFullImageScreen(backStack, uiState.avatar) },
        onFollowClick = viewModel::onFollowClick,
        onBlockClick = viewModel::onBlockClick,
        onUnblockClick = viewModel::onUnblockClick,
        onUnfollowClick = viewModel::onUnfollowClick,
        onFollowerClick = {
            backStack.add(
                UserListScreenNavKey(
                    locator = locator,
                    type = UserListType.FOLLOWERS,
                    did = did
                )
            )
        },
        onFollowingClick = {
            backStack.add(
                UserListScreenNavKey(
                    locator = locator,
                    type = UserListType.FOLLOWING,
                    did = did
                )
            )
        },
        onOpenInBrowserClick = {
            uiState.userHomePageUrl?.let { browserLauncher.launchWebTabInApp(coroutineScope, it) }
        },
        onCopyLinkClick = {
            uiState.userHomePageUrl?.let { activityTextHandler.copyText(it) }
        },
        onEditProfileClick = { backStack.add(EditProfileScreenNavKey(locator = locator)) },
        onMuteClick = { viewModel.onMuteClick(true) },
        onUnmuteClick = { viewModel.onMuteClick(false) },
        onBlockedUserListClick = {
            backStack.add(
                UserListScreenNavKey(
                    locator = locator,
                    type = UserListType.BLOCKED,
                    did = did
                )
            )
        },
        onMuteUserListClick = {
            backStack.add(
                UserListScreenNavKey(
                    locator = locator,
                    type = UserListType.MUTED,
                    did = did
                )
            )
        },
        onHashtagClick = { tag ->
            backStack.add(
                HomeFeedsScreenNavKey.create(
                    feeds = BlueskyFeeds.Hashtags(tag),
                    locator = locator,
                )
            )
        },
        onFollowingFeedsClick = {
            backStack.add(
                BskyFollowingFeedsPageNavKey(
                    contentId = null,
                    locator = locator,
                )
            )
        },
        onLogoutClick = viewModel::onLogoutClick,
    )
    ConsumeSnackbarFlow(snackBarState, viewModel.snackBarMessage)
    LaunchedEffect(Unit) { viewModel.onPageResume() }
    ConsumeFlow(viewModel.finishPageFlow) { backStack.removeLastOrNull() }
}

@Composable
private fun UserDetailContent(
    uiState: BskyUserDetailUiState,
    snackbarHostState: SnackbarHostState,
    locator: PlatformLocator,
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
        val preSharedElementConfig = LocalStatusSharedElementConfig.current
        val sharedElementConfig = remember(preSharedElementConfig) {
            preSharedElementConfig.copy(label = "user-timeline")
        }
        val nestedTabConnection = remember { NestedTabConnection() }
        CompositionLocalProvider(
            LocalNestedTabConnection provides nestedTabConnection,
            LocalStatusSharedElementConfig provides sharedElementConfig,
        ) {
            val contentScrollInProgress by nestedTabConnection.contentScrollInpProgress.collectAsState()
            HorizontalPagerWithTab(
                tabList = tabs,
                pagerUserScrollEnabled = !contentScrollInProgress,
            )
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

private fun openFullImageScreen(backStack: NavBackStack<NavKey>, url: String?) {
    if (url.isNullOrEmpty()) return
    backStack.add(
        ImageViewerScreenNavKey(
            selectedIndex = 0,
            imageList = listOf(ImageViewerImage(url = url)),
        )
    )
}
