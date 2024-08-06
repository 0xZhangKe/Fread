package com.zhangke.fread.activitypub.app.internal.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubRelationshipEntity
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.utils.SystemUtils
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.utils.formatAsCount
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.ActivityPubDataElements
import com.zhangke.fread.activitypub.app.internal.composable.ScrollUpTopBarLayout
import com.zhangke.fread.activitypub.app.internal.screen.account.EditAccountInfoScreen
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineRoute
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.about.UserAboutTab
import com.zhangke.fread.activitypub.app.internal.screen.user.follow.FollowScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.mute.MutedUserListScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.timeline.UserTimelineTab
import com.zhangke.fread.activitypub.app.internal.screen.user.timeline.UserTimelineTabType
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.android.span.LinkSpan
import com.zhangke.fread.status.ui.action.DropDownCopyLinkItem
import com.zhangke.fread.status.ui.action.DropDownOpenInBrowserItem
import com.zhangke.fread.status.ui.action.ModalDropdownMenuItem
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NestedTabConnection
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

@Destination(UserDetailRoute.ROUTE)
data class UserDetailScreen(
    @Router val route: String = "",
    private val role: IdentityRole? = null,
    private val webFinger: WebFinger? = null,
) : BaseScreen() {

    override val key: ScreenKey
        get() = route

    @Composable
    override fun Content() {
        super.Content()
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val transparentNavigator = LocalTransparentNavigator.current
        val (role, userUri, webFinger) = remember(route, role, webFinger) {
            if (role != null && webFinger != null) {
                Triple(role, null, webFinger)
            } else {
                UserDetailRoute.parseRoute(route)
            }
        }
        val viewModel = getViewModel<UserDetailContainerViewModel>()
            .getViewModel(role, userUri, webFinger)
        val uiState by viewModel.uiState.collectAsState()
        UserDetailContent(
            uiState = uiState,
            messageFlow = viewModel.messageFlow,
            onBackClick = navigator::pop,
            onFollowAccountClick = viewModel::onFollowClick,
            onUnfollowAccountClick = viewModel::onUnfollowClick,
            onAcceptClick = viewModel::onAcceptClick,
            onRejectClick = viewModel::onRejectClick,
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
                        val screen = ImageViewerScreen(
                            selectedIndex = 0,
                            imageList = listOf(ImageViewerScreen.Image(url = it)),
                        )
                        transparentNavigator.push(screen)
                    }
            },
            onBannerClick = {
                uiState.accountUiState
                    ?.account
                    ?.header
                    ?.let {
                        val screen = ImageViewerScreen(
                            selectedIndex = 0,
                            imageList = listOf(ImageViewerScreen.Image(url = it)),
                        )
                        transparentNavigator.push(screen)
                    }
            },
            onOpenInBrowserClick = {
                uiState.accountUiState?.account?.url?.let {
                    BrowserLauncher.launchWebTabInApp(context, it)
                }
            },
            onCopyLinkClick = {
                uiState.accountUiState?.account?.url?.let {
                    SystemUtils.copyText(context, it)
                }
            },
            onEditClick = {
                uiState.userInsight
                    ?.let {
                        navigator.push(EditAccountInfoScreen(it.uri))
                    }
            },
            onFollowerClick = {
                if (uiState.userInsight != null) {
                    val screen = FollowScreen(
                        role = uiState.role,
                        userUri = uiState.userInsight!!.uri,
                        isFollowing = false,
                    )
                    navigator.push(screen)
                }
            },
            onFollowingClick = {
                if (uiState.userInsight != null) {
                    val screen = FollowScreen(
                        role = uiState.role,
                        userUri = uiState.userInsight!!.uri,
                        isFollowing = true,
                    )
                    navigator.push(screen)
                }
            },
            onNewNoteSet = viewModel::onNewNoteSet,
            onMaybeHashtagTargetClick = {
                navigator.push(
                    HashtagTimelineScreen(
                        HashtagTimelineRoute.buildRoute(
                            role = uiState.role,
                            hashtag = it.hashtag
                        )
                    )
                )
            },
            onUnmuteUserClick = viewModel::onUnmuteUserClick,
            onMuteUserClick = viewModel::onMuteUserClick,
            onMuteUserListClick = {
                navigator.push(MutedUserListScreen(role))
            },
        )
    }

    @Composable
    private fun UserDetailContent(
        uiState: UserDetailUiState,
        messageFlow: SharedFlow<TextString>,
        onBackClick: () -> Unit,
        onBannerClick: () -> Unit,
        onAvatarClick: () -> Unit,
        onMuteUserClick: () -> Unit,
        onUnmuteUserClick: () -> Unit,
        onUnblockClick: () -> Unit,
        onFollowAccountClick: () -> Unit,
        onUnfollowAccountClick: () -> Unit,
        onCancelFollowRequestClick: () -> Unit,
        onAcceptClick: () -> Unit,
        onRejectClick: () -> Unit,
        onBlockClick: () -> Unit,
        onBlockDomainClick: () -> Unit,
        onUnblockDomainClick: () -> Unit,
        onOpenInBrowserClick: () -> Unit,
        onCopyLinkClick: () -> Unit,
        onEditClick: () -> Unit,
        onFollowerClick: () -> Unit,
        onFollowingClick: () -> Unit,
        onNewNoteSet: (String) -> Unit,
        onMaybeHashtagTargetClick: (LinkSpan.LinkTarget.MaybeHashtagTarget) -> Unit,
        onMuteUserListClick: () -> Unit,
    ) {
        val contentCanScrollBackward = remember {
            mutableStateOf(false)
        }
        val snackBarHost = rememberSnackbarHostState()
        ConsumeSnackbarFlow(hostState = snackBarHost, messageTextFlow = messageFlow)
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    modifier = Modifier.navigationBarsPadding(),
                    hostState = snackBarHost,
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPaddings ->
            val accountUiState = uiState.accountUiState
            val account = accountUiState?.account
            ScrollUpTopBarLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings),
                topBarContent = { progress ->
                    DetailTopBar(
                        title = accountUiState?.userName ?: RichText.empty,
                        progress = progress,
                        onBackClick = onBackClick,
                        actions = {
                            ToolbarActions(
                                uiState = uiState,
                                onBlockClick = onBlockClick,
                                onBlockDomainClick = onBlockDomainClick,
                                onUnblockDomainClick = onUnblockDomainClick,
                                onOpenInBrowserClick = onOpenInBrowserClick,
                                onEditClick = onEditClick,
                                onNewNoteSet = onNewNoteSet,
                                onCopyLinkClick = onCopyLinkClick,
                                onMuteUserClick = onMuteUserClick,
                                onUnmuteUserClick = onUnmuteUserClick,
                                onMuteUserListClick = onMuteUserListClick,
                            )
                        },
                    )
                },
                headerContent = { progress ->
                    val context = LocalContext.current
                    DetailHeaderContent(
                        progress = progress,
                        loading = uiState.loading,
                        banner = account?.header,
                        avatar = account?.avatar,
                        title = accountUiState?.userName,
                        description = accountUiState?.description,
                        privateNote = uiState.relationship?.note,
                        acctLine = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                SelectionContainer {
                                    Text(
                                        modifier = Modifier,
                                        text = account?.prettyAcct.orEmpty(),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                                if (uiState.relationship?.followedBy == true) {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 4.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.surfaceContainer,
                                                shape = RoundedCornerShape(2.dp),
                                            )
                                            .padding(horizontal = 4.dp),
                                        text = stringResource(R.string.activity_pub_user_detail_follows_you),
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            }
                        },
                        followInfo = {
                            FollowInfoLine(
                                modifier = Modifier,
                                account = account,
                                onFollowerClick = onFollowerClick,
                                onFollowingClick = onFollowingClick,
                            )
                        },
                        relationship = if (uiState.isAccountOwner) null else uiState.relationship?.toUiState(),
                        onBannerClick = onBannerClick,
                        onAvatarClick = onAvatarClick,
                        onUnblockClick = onUnblockClick,
                        onCancelFollowRequestClick = onCancelFollowRequestClick,
                        onAcceptClick = onAcceptClick,
                        onRejectClick = onRejectClick,
                        onFollowAccountClick = onFollowAccountClick,
                        onUnfollowAccountClick = onUnfollowAccountClick,
                        onUrlClick = {
                            BrowserLauncher.launchWebTabInApp(context, it, role)
                        },
                        onMaybeHashtagTargetClick = onMaybeHashtagTargetClick,
                    )
                },
                contentCanScrollBackward = contentCanScrollBackward,
            ) {
                if (uiState.userInsight != null) {
                    val tabs: List<PagerTab> = remember(uiState) {
                        listOf(
                            UserTimelineTab(
                                tabType = UserTimelineTabType.POSTS,
                                role = uiState.role,
                                userWebFinger = uiState.userInsight.webFinger,
                                contentCanScrollBackward = contentCanScrollBackward,
                            ),
                            UserTimelineTab(
                                tabType = UserTimelineTabType.REPLIES,
                                role = uiState.role,
                                userWebFinger = uiState.userInsight.webFinger,
                                contentCanScrollBackward = contentCanScrollBackward,
                            ),
                            UserTimelineTab(
                                tabType = UserTimelineTabType.MEDIA,
                                role = uiState.role,
                                userWebFinger = uiState.userInsight.webFinger,
                                contentCanScrollBackward = contentCanScrollBackward,
                            ),
                            UserAboutTab(
                                contentCanScrollBackward = contentCanScrollBackward,
                                role = uiState.role,
                                userWebFinger = uiState.userInsight.webFinger,
                            ),
                        )
                    }
                    val nestedTabConnection = remember {
                        NestedTabConnection()
                    }
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides snackBarHost,
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
    private fun FollowInfoLine(
        modifier: Modifier,
        account: ActivityPubAccountEntity?,
        onFollowerClick: () -> Unit,
        onFollowingClick: () -> Unit,
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CountInfoItem(
                count = account?.followersCount,
                descId = R.string.activity_pub_user_detail_follower_info,
                onClick = onFollowerClick,
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                text = "·",
                style = MaterialTheme.typography.bodySmall,
            )
            CountInfoItem(
                count = account?.followingCount,
                descId = R.string.activity_pub_user_detail_following_info,
                onClick = onFollowingClick,
            )
            Text(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                text = "·",
                style = MaterialTheme.typography.bodySmall,
            )
            CountInfoItem(
                count = account?.statusesCount,
                descId = R.string.activity_pub_user_detail_posts,
            )
        }
    }

    @Composable
    private fun CountInfoItem(
        count: Int?,
        descId: Int,
        onClick: (() -> Unit)? = null,
    ) {
        val descSuffix = stringResource(descId)
        val info = remember(count) {
            if (count == null) {
                buildAnnotatedString { append("    ") }
            } else {
                buildCountedDesc(count, descSuffix)
            }
        }
        Text(
            modifier = Modifier.clickable(count != null && onClick != null) {
                onClick?.invoke()
            },
            text = info,
            style = MaterialTheme.typography.bodySmall,
        )
    }

    private fun buildCountedDesc(count: Int, desc: String): AnnotatedString {
        val formattedCount = count.formatAsCount()
        return buildAnnotatedString {
            append(formattedCount)
            addStyle(
                style = SpanStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                ),
                start = 0,
                end = formattedCount.length,
            )
            append(" ")
            append(desc)
        }
    }

    @Composable
    private fun ToolbarActions(
        uiState: UserDetailUiState,
        onBlockClick: () -> Unit,
        onBlockDomainClick: () -> Unit,
        onUnblockDomainClick: () -> Unit,
        onOpenInBrowserClick: () -> Unit,
        onCopyLinkClick: () -> Unit,
        onEditClick: () -> Unit,
        onNewNoteSet: (String) -> Unit,
        onMuteUserClick: () -> Unit,
        onUnmuteUserClick: () -> Unit,
        onMuteUserListClick: () -> Unit,
    ) {
        val accountUiState = uiState.accountUiState ?: return
        if (uiState.isAccountOwner) {
            SimpleIconButton(
                onClick = onEditClick,
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Profile",
            )
            Box(modifier = Modifier.width(16.dp))
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
        var showBlockDomainConfirmDialog by remember {
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
                reportClick(ActivityPubDataElements.USER_DETAIL_OPEN_IN_BROWSER)
                showMorePopup = false
                onOpenInBrowserClick()
            }
            DropDownCopyLinkItem {
                reportClick(ActivityPubDataElements.USER_DETAIL_COPY_LINK)
                showMorePopup = false
                onCopyLinkClick()
            }
            val isAccountOwner = uiState.isAccountOwner
            if (isAccountOwner) {
                SelfAccountActions(
                    onMuteUserListClick = {
                        showMorePopup = false
                        onMuteUserListClick()
                    },
                )
            }
            val relationship = uiState.relationship
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
                content = stringResource(R.string.activity_pub_user_detail_dialog_content_block),
                onConfirm = {
                    showBlockUserConfirmDialog = false
                    onBlockClick()
                },
                onDismissRequest = { showBlockUserConfirmDialog = false },
            )
        }
        if (showBlockDomainConfirmDialog) {
            AlertConfirmDialog(
                content = stringResource(R.string.activity_pub_user_detail_dialog_content_block_domain),
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
            text = stringResource(R.string.activity_pub_user_detail_menu_edit_private_note),
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
                title = stringResource(R.string.activity_pub_user_detail_menu_edit_private_note),
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
                                text = stringResource(R.string.activity_pub_user_detail_menu_edit_private_note)
                            )
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.activity_pub_user_detail_menu_edit_private_note_dialog_hint)
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
        onMuteUserListClick: () -> Unit,
    ) {
        ModalDropdownMenuItem(
            text = stringResource(R.string.activity_pub_user_menu_muted_user_list),
            imageVector = Icons.Default.Block,
            onClick = onMuteUserListClick,
        )
    }

    @Composable
    private fun OtherAccountActions(
        uiState: UserDetailUiState,
        account: UserDetailAccountUiState,
        relationship: ActivityPubRelationshipEntity,
        onNewNoteSet: (String) -> Unit,
        onUnmuteClick: () -> Unit,
        onDismissMorePopupRequest: () -> Unit,
        onUnblockDomainClick: () -> Unit,
        onShowBlockUserConfirmDialog: () -> Unit,
        onShowBlockDomainConfirmDialog: () -> Unit,
        onShowMuteDialogClick: () -> Unit,
    ) {
        EditPrivateNoteItem(
            note = uiState.relationship?.note.orEmpty(),
            onDismissRequest = onDismissMorePopupRequest,
            onNewNoteSet = onNewNoteSet,
        )
        val fixedName = account.account.displayName.take(10)
        val muteOrUnmuteText = if (relationship.muting) {
            stringResource(R.string.activity_pub_user_detail_menu_unmute_user, fixedName)
        } else {
            stringResource(R.string.activity_pub_user_detail_menu_mute_user, fixedName)
        }
        ModalDropdownMenuItem(
            text = muteOrUnmuteText,
            imageVector = Icons.Default.Block,
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
                text = stringResource(R.string.activity_pub_user_detail_menu_block, fixedName),
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
                stringResource(R.string.activity_pub_user_detail_menu_unblock_domain, host)
            } else {
                stringResource(R.string.activity_pub_user_detail_menu_block_domain, host)
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
                    text = stringResource(R.string.activity_pub_mute_user_bottom_sheet_title),
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
                    role = stringResource(R.string.activity_pub_mute_user_bottom_sheet_role1)
                )

                MuteUserRoleItem(
                    icon = Icons.Default.VisibilityOff,
                    role = stringResource(R.string.activity_pub_mute_user_bottom_sheet_role2)
                )

                MuteUserRoleItem(
                    icon = Icons.Default.AlternateEmail,
                    role = stringResource(R.string.activity_pub_mute_user_bottom_sheet_role3)
                )

                MuteUserRoleItem(
                    icon = ImageVector.vectorResource(com.zhangke.fread.statusui.R.drawable.ic_status_forward),
                    role = stringResource(R.string.activity_pub_mute_user_bottom_sheet_role4)
                )

                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .height(40.dp),
                    onClick = onConfirmClick,
                ) {
                    Text(text = stringResource(R.string.activity_pub_mute_user_bottom_sheet_btn_mute))
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
                    Text(text = stringResource(com.zhangke.fread.framework.R.string.cancel))
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

    private val ActivityPubAccountEntity.prettyAcct: String
        get() {
            val acct = this.acct
            return if (acct.isNotEmpty() && !acct.contains('@')) {
                "@$acct"
            } else {
                acct
            }
        }

    private fun ActivityPubRelationshipEntity?.toUiState(): RelationshipUiState {
        return when {
            this == null -> RelationshipUiState.UNKNOWN
            this.blockedBy -> RelationshipUiState.BLOCKED_BY
            this.blocking -> RelationshipUiState.BLOCKING
            this.requested -> RelationshipUiState.REQUESTED
            this.requestedBy -> RelationshipUiState.REQUEST_BY
            this.following -> RelationshipUiState.FOLLOWING
            this.followedBy -> RelationshipUiState.FOLLOWED_BY
            else -> RelationshipUiState.CAN_FOLLOW
        }
    }
}
