package com.zhangke.fread.activitypub.app.internal.screen.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.PopupMenu
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.date.DateParser
import com.zhangke.framework.nav.ContentPaddingsHorizontalPagerWithTab
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.screen.account.EditAccountInfoScreenNavKey
import com.zhangke.fread.activitypub.app.internal.screen.filters.list.FiltersListScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.list.CreatedListsScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.search.SearchStatusScreenNavKey
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListType
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListTabStatusListScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListType
import com.zhangke.fread.activitypub.app.internal.screen.user.tags.TagListScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.user.timeline.UserTimelineTab
import com.zhangke.fread.activitypub.app.internal.screen.user.timeline.UserTimelineTabType
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.browser.launchWebTabInApp
import com.zhangke.fread.common.handler.LocalTextHandler
import com.zhangke.fread.common.utils.formatDate
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerImage
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreenNavKey
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.Relationships
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.action.DropDownCopyLinkItem
import com.zhangke.fread.status.ui.action.DropDownOpenInBrowserItem
import com.zhangke.fread.status.ui.action.DropDownOpenOriginalInstanceItem
import com.zhangke.fread.status.ui.action.ModalDropdownMenuItem
import com.zhangke.fread.status.ui.common.DetailPageScaffold
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NestedTabConnection
import com.zhangke.fread.status.ui.common.RelationshipStateButton
import com.zhangke.fread.status.ui.common.UserFollowLine
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.user.UserHandleLine
import com.zhangke.fread.status.uri.FormalUri
import com.zhangke.fread.statusui.ic_status_forward
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Serializable
data class UserDetailScreenKey(
    val locator: PlatformLocator,
    val userUri: FormalUri? = null,
    val webFinger: WebFinger? = null,
    val userId: String? = null,
) : NavKey

@Composable
fun UserDetailScreen(
    viewModel: UserDetailContainerViewModel,
    locator: PlatformLocator,
    userUri: FormalUri? = null,
    webFinger: WebFinger? = null,
    userId: String? = null,
) {
    val backstack = LocalNavBackStack.currentOrThrow
    val browserLauncher = LocalActivityBrowserLauncher.current
    val activityTextHandler = LocalTextHandler.current
    val viewModel = viewModel.getViewModel(locator, userUri, webFinger, userId)
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    UserDetailContent(
        uiState = uiState,
        messageFlow = viewModel.messageFlow,
        userId = userId,
        onFavouritesClick = {
            backstack.add(
                StatusListScreenKey(
                    locator = locator,
                    type = StatusListType.FAVOURITES
                )
            )
        },
        onSearchClick = {
            uiState.accountUiState?.account?.id?.let { userId ->
                backstack.add(
                    SearchStatusScreenNavKey(
                        locator = uiState.locator,
                        userId = userId,
                    )
                )
            }
        },
        onBookmarksClick = {
            backstack.add(StatusListScreenKey(locator = locator, type = StatusListType.BOOKMARKS))
        },
        onBackClick = backstack::removeLastOrNull,
        onFollowAccountClick = viewModel::onFollowClick,
        onUnfollowAccountClick = viewModel::onUnfollowClick,
        onCancelFollowRequestClick = viewModel::onCancelFollowRequestClick,
        onUnblockClick = viewModel::onUnblockClick,
        onBlockClick = viewModel::onBlockClick,
        onBlockDomainClick = viewModel::onBlockDomainClick,
        onUnblockDomainClick = viewModel::onUnblockDomainClick,
        onAvatarClick = {
            uiState.accountUiState
                ?.account
                ?.avatar
                ?.let {
                    val screenKey = ImageViewerScreenNavKey(
                        selectedIndex = 0,
                        imageList = listOf(ImageViewerImage(url = it)),
                    )
                    backstack.add(screenKey)
                }
        },
        onBannerClick = {
            uiState.accountUiState
                ?.account
                ?.header
                ?.let {
                    val screen = ImageViewerScreenNavKey(
                        selectedIndex = 0,
                        imageList = listOf(ImageViewerImage(url = it)),
                    )
                    backstack.add(screen)
                }
        },
        onOpenInBrowserClick = {
            uiState.accountUiState?.account?.url?.let {
                browserLauncher.launchWebTabInApp(coroutineScope, it)
            }
        },
        onCopyLinkClick = {
            uiState.accountUiState?.account?.url?.let {
                activityTextHandler.copyText(it)
            }
        },
        onOpenOriginalInstanceClick = {
            uiState.accountUiState?.account?.url?.let { FormalBaseUrl.parse(it) }?.let {
                browserLauncher.launchWebTabInApp(
                    scope = coroutineScope,
                    url = it.toString(),
                    locator = locator,
                    checkAppSupportPage = true,
                )
            }
        },
        onEditClick = {
            uiState.userInsight
                ?.let {
                    backstack.add(
                        EditAccountInfoScreenNavKey(
                            baseUrl = it.baseUrl.toString(),
                            accountUri = it.uri.toString(),
                        )
                    )
                }
        },
        onFollowerClick = {
            if (uiState.userInsight != null) {
                val key = UserListScreenKey(
                    type = UserListType.FOLLOWERS,
                    locator = uiState.locator,
                    userUri = uiState.userInsight!!.uri,
                    userId = uiState.accountUiState?.account?.id ?: userId,
                )
                backstack.add(key)
            }
        },
        onFollowingClick = {
            if (uiState.userInsight != null) {
                val screen = UserListScreenKey(
                    type = UserListType.FOLLOWING,
                    locator = uiState.locator,
                    userUri = uiState.userInsight!!.uri,
                    userId = uiState.accountUiState?.account?.id ?: userId,
                )
                backstack.add(screen)
            }
        },
        onNewNoteSet = viewModel::onNewNoteSet,
        onMaybeHashtagClick = {
            backstack.add(
                HashtagTimelineScreenKey(
                    locator = uiState.locator,
                    hashtag = it.removePrefix("#"),
                )
            )
        },
        onUnmuteUserClick = viewModel::onUnmuteUserClick,
        onMuteUserClick = viewModel::onMuteUserClick,
        onMuteUserListClick = {
            backstack.add(
                UserListScreenKey(
                    locator = locator,
                    type = UserListType.MUTED,
                    userId = uiState.accountUiState?.account?.id ?: userId,
                )
            )
        },
        onBlockedUserListClick = {
            backstack.add(
                UserListScreenKey(
                    locator = locator,
                    type = UserListType.BLOCKED,
                    userId = uiState.accountUiState?.account?.id ?: userId,
                )
            )
        },
        onFollowedHashtagsListClick = {
            backstack.add(TagListScreenKey(locator))
        },
        onFilterClick = {
            backstack.add(FiltersListScreenKey(uiState.locator))
        },
        onCreatedListClick = {
            backstack.add(CreatedListsScreenKey(uiState.locator))
        },
        onLogoutClick = {
            viewModel.onLogoutClick()
        },
    )
    ConsumeFlow(viewModel.finishPageFlow) {
        backstack.removeLastOrNull()
    }
}

@Composable
private fun UserDetailContent(
    uiState: UserDetailUiState,
    messageFlow: SharedFlow<TextString>,
    userId: String?,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavouritesClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onBannerClick: () -> Unit,
    onAvatarClick: () -> Unit,
    onMuteUserClick: () -> Unit,
    onUnmuteUserClick: () -> Unit,
    onUnblockClick: () -> Unit,
    onFollowAccountClick: () -> Unit,
    onUnfollowAccountClick: () -> Unit,
    onCancelFollowRequestClick: () -> Unit,
    onBlockClick: () -> Unit,
    onBlockDomainClick: () -> Unit,
    onUnblockDomainClick: () -> Unit,
    onOpenInBrowserClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onOpenOriginalInstanceClick: () -> Unit,
    onEditClick: () -> Unit,
    onFollowerClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onNewNoteSet: (String) -> Unit,
    onMaybeHashtagClick: (String) -> Unit,
    onMuteUserListClick: () -> Unit,
    onBlockedUserListClick: () -> Unit,
    onFollowedHashtagsListClick: () -> Unit,
    onFilterClick: () -> Unit,
    onCreatedListClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val accountUiState = uiState.accountUiState
    val account = accountUiState?.account
    val browserLauncher = LocalActivityBrowserLauncher.current
    val contentCanScrollBackward = remember { mutableStateOf(false) }
    val snackBarHost = rememberSnackbarHostState()
    val coroutineScope = rememberCoroutineScope()
    ConsumeSnackbarFlow(hostState = snackBarHost, messageTextFlow = messageFlow)
    DetailPageScaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHostState = snackBarHost,
        title = accountUiState?.userName ?: RichText.empty,
        avatar = account?.avatar.orEmpty(),
        banner = account?.header,
        description = accountUiState?.description,
        privateNote = uiState.personalNote,
        loading = uiState.loading,
        contentCanScrollBackward = contentCanScrollBackward,
        onBannerClick = onBannerClick,
        onAvatarClick = onAvatarClick,
        onUrlClick = {
            browserLauncher.launchWebTabInApp(coroutineScope, it, uiState.locator)
        },
        onMaybeHashtagClick = onMaybeHashtagClick,
        onBackClick = onBackClick,
        topBarActions = {
            ToolbarActions(
                uiState = uiState,
                onFavouritesClick = onFavouritesClick,
                onBlockClick = onBlockClick,
                onSearchClick = onSearchClick,
                onBookmarksClick = onBookmarksClick,
                onBlockDomainClick = onBlockDomainClick,
                onUnblockDomainClick = onUnblockDomainClick,
                onOpenInBrowserClick = onOpenInBrowserClick,
                onOpenOriginalInstanceClick = onOpenOriginalInstanceClick,
                onNewNoteSet = onNewNoteSet,
                onCopyLinkClick = onCopyLinkClick,
                onMuteUserClick = onMuteUserClick,
                onUnmuteUserClick = onUnmuteUserClick,
                onMuteUserListClick = onMuteUserListClick,
                onBlockedUserListClick = onBlockedUserListClick,
                onFollowedHashtagsListClick = onFollowedHashtagsListClick,
                onFilterClick = onFilterClick,
                onCreatedListClick = onCreatedListClick,
                onLogoutClick = onLogoutClick,
            )
        },
        handleLine = {
            UserHandleLine(
                modifier = Modifier,
                handle = account?.prettyAcct.orEmpty(),
                bot = account?.bot == true,
                followedBy = uiState.relationships?.followedBy == true
            )
        },
        followInfoLine = {
            UserFollowLine(
                modifier = Modifier,
                followersCount = account?.followersCount?.toLong(),
                followingCount = account?.followingCount?.toLong(),
                statusesCount = account?.statusesCount?.toLong(),
                onFollowerClick = onFollowerClick,
                onFollowingClick = onFollowingClick,
            )
        },
        topDetailContentAction = {
            if (uiState.isAccountOwner) {
                FilledTonalButton(
                    onClick = onEditClick,
                ) {
                    Text(
                        text = stringResource(LocalizedString.statusUiEditProfile)
                    )
                }
            } else if (uiState.relationships != null) {
                RelationshipStateButton(
                    modifier = Modifier,
                    relationship = uiState.relationships,
                    onFollowClick = onFollowAccountClick,
                    onUnfollowClick = onUnfollowAccountClick,
                    onCancelFollowRequestClick = onCancelFollowRequestClick,
                    onUnblockClick = onUnblockClick,
                )
            }
        },
        bottomArea = if (uiState.accountUiState?.account != null) {
            {
                UserAboutCard(
                    locator = uiState.locator,
                    account = uiState.accountUiState.account,
                    emojis = uiState.accountUiState.emojis,
                )
            }
        } else {
            null
        },
    ) { progress ->
        if (uiState.userInsight != null) {
            val tabs: List<Tab> = remember(
                uiState.userInsight,
                uiState.locator,
                uiState.isAccountOwner,
            ) {
                buildTabList(
                    webFinger = uiState.userInsight.webFinger,
                    locator = uiState.locator,
                    userId = userId,
                    isAccountOwner = uiState.isAccountOwner,
                    contentCanScrollBackward = contentCanScrollBackward,
                )
            }
            val nestedTabConnection = remember { NestedTabConnection() }
            CompositionLocalProvider(
                LocalNestedTabConnection provides nestedTabConnection,
            ) {
                val contentScrollInProgress by nestedTabConnection.contentScrollInpProgress.collectAsState()
                ContentPaddingsHorizontalPagerWithTab(
                    tabList = tabs,
                    blurEnabled = progress >= 1F,
                    pagerUserScrollEnabled = !contentScrollInProgress,
                )
            }
        }
    }
}

private fun buildTabList(
    locator: PlatformLocator,
    webFinger: WebFinger,
    isAccountOwner: Boolean,
    userId: String?,
    contentCanScrollBackward: MutableState<Boolean>,
): List<Tab> {
    val tabList = mutableListOf<Tab>()
    tabList += UserTimelineTab(
        tabType = UserTimelineTabType.POSTS,
        locator = locator,
        userWebFinger = webFinger,
        contentCanScrollBackward = contentCanScrollBackward,
        userId = userId,
    )
    tabList += UserTimelineTab(
        tabType = UserTimelineTabType.REPLIES,
        locator = locator,
        userWebFinger = webFinger,
        contentCanScrollBackward = contentCanScrollBackward,
        userId = userId,
    )
    tabList += UserTimelineTab(
        tabType = UserTimelineTabType.MEDIA,
        locator = locator,
        userWebFinger = webFinger,
        contentCanScrollBackward = contentCanScrollBackward,
        userId = userId,
    )
    if (isAccountOwner) {
        tabList += StatusListTabStatusListScreen(
            locator = locator,
            type = StatusListType.FAVOURITES,
            contentCanScrollBackward = contentCanScrollBackward,
        )
        tabList += StatusListTabStatusListScreen(
            locator = locator,
            type = StatusListType.BOOKMARKS,
            contentCanScrollBackward = contentCanScrollBackward,
        )
    }
    return tabList
}

@Composable
private fun ToolbarActions(
    uiState: UserDetailUiState,
    onSearchClick: () -> Unit,
    onFavouritesClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onBlockClick: () -> Unit,
    onBlockDomainClick: () -> Unit,
    onUnblockDomainClick: () -> Unit,
    onOpenInBrowserClick: () -> Unit,
    onCopyLinkClick: () -> Unit,
    onOpenOriginalInstanceClick: () -> Unit,
    onNewNoteSet: (String) -> Unit,
    onMuteUserClick: () -> Unit,
    onUnmuteUserClick: () -> Unit,
    onMuteUserListClick: () -> Unit,
    onBlockedUserListClick: () -> Unit,
    onFollowedHashtagsListClick: () -> Unit,
    onFilterClick: () -> Unit,
    onCreatedListClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val accountUiState = uiState.accountUiState ?: return
    SimpleIconButton(
        onClick = onSearchClick,
        imageVector = Icons.Default.Search,
        contentDescription = stringResource(LocalizedString.search),
    )
    if (uiState.isAccountOwner) {
        SimpleIconButton(
            onClick = onCreatedListClick,
            imageVector = Icons.AutoMirrored.Outlined.ListAlt,
            contentDescription = stringResource(LocalizedString.activity_pub_created_list_title),
        )
    }
    var showMorePopup by remember { mutableStateOf(false) }
    SimpleIconButton(
        onClick = { showMorePopup = true },
        imageVector = Icons.Default.MoreVert,
        contentDescription = "More Options"
    )
    var showBlockUserConfirmDialog by remember {
        mutableStateOf(false)
    }
    var showBlockDomainConfirmDialog by remember {
        mutableStateOf(false)
    }
    var showMuteDialog by remember {
        mutableStateOf(false)
    }
    PopupMenu(
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
        DropDownOpenOriginalInstanceItem {
            showMorePopup = false
            onOpenOriginalInstanceClick()
        }
        val isAccountOwner = uiState.isAccountOwner
        if (isAccountOwner) {
            SelfAccountActions(
                onBookmarksClick = {
                    showMorePopup = false
                    onBookmarksClick()
                },
                onFavouritesClick = {
                    showMorePopup = false
                    onFavouritesClick()
                },
                onBlockedUserListClick = {
                    showMorePopup = false
                    onBlockedUserListClick()
                },
                onMuteUserListClick = {
                    showMorePopup = false
                    onMuteUserListClick()
                },
                onFollowedHashtagsListClick = {
                    showMorePopup = false
                    onFollowedHashtagsListClick()
                },
                onFilterClick = {
                    showMorePopup = false
                    onFilterClick()
                },
                onLogoutClick = {
                    showMorePopup = false
                    onLogoutClick()
                },
            )
        }
        val relationship = uiState.relationships
        if (!isAccountOwner && relationship != null) {
            OtherAccountActions(
                uiState = uiState,
                account = accountUiState,
                relationship = relationship,
                onNewNoteSet = onNewNoteSet,
                onDismissMorePopupRequest = {
                    showMorePopup = false
                },
                onShowBlockUserConfirmDialog = {
                    showBlockUserConfirmDialog = true
                },
                onShowBlockDomainConfirmDialog = {
                    showBlockDomainConfirmDialog = true
                },
                onUnblockDomainClick = onUnblockDomainClick,
                onUnmuteClick = onUnmuteUserClick,
                onShowMuteDialogClick = {
                    showMuteDialog = true
                },
            )
        }
    }
    if (showBlockUserConfirmDialog) {
        AlertConfirmDialog(
            content = stringResource(LocalizedString.activity_pub_user_detail_dialog_content_block),
            onConfirm = {
                showBlockUserConfirmDialog = false
                onBlockClick()
            },
            onDismissRequest = { showBlockUserConfirmDialog = false },
        )
    }
    if (showBlockDomainConfirmDialog) {
        AlertConfirmDialog(
            content = stringResource(LocalizedString.activity_pub_user_detail_dialog_content_block_domain),
            onConfirm = {
                showBlockDomainConfirmDialog = false
                onBlockDomainClick()
            },
            onDismissRequest = { showBlockDomainConfirmDialog = false },
        )
    }
    if (showMuteDialog) {
        MuteUserBottomSheetDialog(
            account = accountUiState,
            onDismissRequest = { showMuteDialog = false },
            onConfirmClick = {
                showMuteDialog = false
                onMuteUserClick()
            },
        )
    }
}

@Composable
private fun EditPrivateNoteItem(
    note: String,
    onDismissRequest: () -> Unit,
    onNewNoteSet: (String) -> Unit,
) {
    var showEditDialog by remember {
        mutableStateOf(false)
    }
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.activity_pub_user_detail_menu_edit_private_note),
        imageVector = Icons.Default.Edit,
        onClick = {
            showEditDialog = true
        },
    )
    if (showEditDialog) {
        var inputtingNote by remember { mutableStateOf(note) }
        FreadDialog(
            onDismissRequest = {
                onDismissRequest()
                showEditDialog = false
            },
            title = stringResource(LocalizedString.activity_pub_user_detail_menu_edit_private_note),
            content = {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
                    value = inputtingNote,
                    onValueChange = {
                        inputtingNote = it
                    },
                    label = {
                        Text(
                            text = stringResource(LocalizedString.activity_pub_user_detail_menu_edit_private_note)
                        )
                    },
                    placeholder = {
                        Text(
                            text = stringResource(LocalizedString.activity_pub_user_detail_menu_edit_private_note_dialog_hint)
                        )
                    },
                )
            },
            onNegativeClick = {
                onDismissRequest()
                showEditDialog = false
            },
            onPositiveClick = {
                onDismissRequest()
                showEditDialog = false
                onNewNoteSet(inputtingNote)
            },
        )
    }
}

@Composable
private fun SelfAccountActions(
    onBookmarksClick: () -> Unit,
    onFavouritesClick: () -> Unit,
    onBlockedUserListClick: () -> Unit,
    onMuteUserListClick: () -> Unit,
    onFollowedHashtagsListClick: () -> Unit,
    onFilterClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.activity_pub_favourites_list_title),
        onClick = onFavouritesClick,
        imageVector = Icons.Default.Favorite,
    )
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.activity_pub_bookmarks_list_title),
        onClick = onBookmarksClick,
        imageVector = Icons.Default.Bookmarks,
    )
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.activity_pub_followed_tags_screen_title),
        onClick = onFollowedHashtagsListClick,
        imageVector = Icons.Default.Tag,
    )
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.activity_pub_user_menu_muted_user_list),
        imageVector = Icons.AutoMirrored.Filled.VolumeOff,
        onClick = onMuteUserListClick,
    )
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.activity_pub_user_menu_blocked_user_list),
        imageVector = Icons.Default.Block,
        onClick = onBlockedUserListClick,
    )
    ModalDropdownMenuItem(
        text = stringResource(LocalizedString.activity_pub_filters_list_page_title),
        imageVector = Icons.Default.FilterAlt,
        onClick = onFilterClick,
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
    uiState: UserDetailUiState,
    account: UserDetailAccountUiState,
    relationship: Relationships,
    onNewNoteSet: (String) -> Unit,
    onUnmuteClick: () -> Unit,
    onDismissMorePopupRequest: () -> Unit,
    onUnblockDomainClick: () -> Unit,
    onShowBlockUserConfirmDialog: () -> Unit,
    onShowBlockDomainConfirmDialog: () -> Unit,
    onShowMuteDialogClick: () -> Unit,
) {
    EditPrivateNoteItem(
        note = uiState.personalNote.orEmpty(),
        onDismissRequest = onDismissMorePopupRequest,
        onNewNoteSet = onNewNoteSet,
    )
    val fixedName = account.account.displayName.take(10)
    val muteOrUnmuteText = if (relationship.muting) {
        stringResource(LocalizedString.activity_pub_user_detail_menu_unmute_user, fixedName)
    } else {
        stringResource(LocalizedString.activity_pub_user_detail_menu_mute_user, fixedName)
    }
    ModalDropdownMenuItem(
        text = muteOrUnmuteText,
        imageVector = Icons.AutoMirrored.Filled.VolumeOff,
        onClick = {
            onDismissMorePopupRequest()
            if (relationship.muting) {
                onUnmuteClick()
            } else {
                onShowMuteDialogClick()
            }
        }
    )
    if (!relationship.blocking) {
        ModalDropdownMenuItem(
            text = stringResource(LocalizedString.activity_pub_user_detail_menu_block, fixedName),
            imageVector = Icons.Default.Block,
            onClick = {
                onDismissMorePopupRequest()
                onShowBlockUserConfirmDialog()
            },
        )
    }
    val domainBlocked = uiState.domainBlocked
    val host = uiState.userInsight!!.baseUrl.host
    if (domainBlocked != null) {
        val blockDomainLabel = if (domainBlocked) {
            stringResource(LocalizedString.activity_pub_user_detail_menu_unblock_domain, host)
        } else {
            stringResource(LocalizedString.activity_pub_user_detail_menu_block_domain, host)
        }
        ModalDropdownMenuItem(
            text = blockDomainLabel,
            imageVector = Icons.Default.Block,
            onClick = {
                onDismissMorePopupRequest()
                if (domainBlocked) {
                    onUnblockDomainClick()
                } else {
                    onShowBlockDomainConfirmDialog()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MuteUserBottomSheetDialog(
    account: UserDetailAccountUiState,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        sheetState = state,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        ) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(LocalizedString.activity_pub_mute_user_bottom_sheet_title),
                style = MaterialTheme.typography.titleLarge,
            )

            FreadRichText(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                richText = account.userName,
                fontSizeSp = 16F,
            )

            Text(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .align(Alignment.CenterHorizontally),
                text = account.account.prettyAcct,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(8.dp))

            MuteUserRoleItem(
                icon = Icons.Default.Campaign,
                role = stringResource(LocalizedString.activity_pub_mute_user_bottom_sheet_role1)
            )

            MuteUserRoleItem(
                icon = Icons.Default.VisibilityOff,
                role = stringResource(LocalizedString.activity_pub_mute_user_bottom_sheet_role2)
            )

            MuteUserRoleItem(
                icon = Icons.Default.AlternateEmail,
                role = stringResource(LocalizedString.activity_pub_mute_user_bottom_sheet_role3)
            )

            MuteUserRoleItem(
                icon = vectorResource(com.zhangke.fread.statusui.Res.drawable.ic_status_forward),
                role = stringResource(LocalizedString.activity_pub_mute_user_bottom_sheet_role4)
            )

            Button(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth()
                    .height(40.dp),
                onClick = onConfirmClick,
            ) {
                Text(text = stringResource(LocalizedString.activity_pub_mute_user_bottom_sheet_btn_mute))
            }
            TextButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp),
                onClick = {
                    coroutineScope.launch {
                        state.hide()
                        onDismissRequest()
                    }
                },
            ) {
                Text(text = stringResource(LocalizedString.cancel))
            }
        }
    }
}

@Composable
private fun MuteUserRoleItem(
    icon: ImageVector,
    role: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            modifier = Modifier.weight(1F),
            text = role,
            textAlign = TextAlign.Start,
        )
    }
}

@Composable
private fun UserAboutCard(
    account: ActivityPubAccountEntity,
    locator: PlatformLocator,
    emojis: List<Emoji>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme
                .surfaceContainerHighest
                .copy(alpha = 0.3F),
        ),
    ) {
        SelectionContainer {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                FieldLine(
                    locator = locator,
                    key = stringResource(LocalizedString.activity_pub_user_detail_join_date),
                    value = DateParser.parseOrCurrent(account.createdAt).formatDate(),
                    emojis = emojis,
                )
                for (field in account.fields) {
                    FieldLine(
                        locator = locator,
                        key = field.name,
                        value = field.value,
                        emojis = emojis,
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldLine(
    locator: PlatformLocator,
    key: String,
    value: String,
    emojis: List<Emoji>,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    val coroutineScope = rememberCoroutineScope()
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.weight(2F),
            text = key,
            maxLines = 1,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier.weight(4F),
            contentAlignment = Alignment.CenterEnd,
        ) {
            FreadRichText(
                modifier = Modifier,
                content = value,
                emojis = emojis,
                textAlign = TextAlign.End,
                maxLines = 3,
                onUrlClick = {
                    browserLauncher.launchWebTabInApp(coroutineScope, it, locator)
                },
            )
        }
    }
}

private val ActivityPubAccountEntity.prettyAcct: String
    get() {
        val acct = this.acct
        return if (acct.isNotEmpty() && !acct.contains('@')) {
            "@$acct"
        } else {
            acct
        }
    }
