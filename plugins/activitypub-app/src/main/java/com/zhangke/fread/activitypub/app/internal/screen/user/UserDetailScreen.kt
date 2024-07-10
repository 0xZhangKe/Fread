package com.zhangke.fread.activitypub.app.internal.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.activitypub.entities.ActivityPubRelationshipEntity
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.composable.ScrollUpTopBarLayout
import com.zhangke.fread.activitypub.app.internal.screen.account.EditAccountInfoScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.about.UserAboutTab
import com.zhangke.fread.activitypub.app.internal.screen.user.follow.FollowScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.timeline.UserTimelineTab
import com.zhangke.fread.activitypub.app.internal.screen.user.timeline.UserTimelineTabType
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NestedTabConnection
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import kotlinx.coroutines.flow.SharedFlow

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
        )
    }

    @Composable
    private fun UserDetailContent(
        uiState: UserDetailUiState,
        messageFlow: SharedFlow<TextString>,
        onBackClick: () -> Unit,
        onBannerClick: () -> Unit,
        onAvatarClick: () -> Unit,
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
        onEditClick: () -> Unit,
        onFollowerClick: () -> Unit,
        onFollowingClick: () -> Unit,
    ) {
        val contentCanScrollBackward = remember {
            mutableStateOf(false)
        }
        val snackbarHost = rememberSnackbarHostState()
        ConsumeSnackbarFlow(hostState = snackbarHost, messageTextFlow = messageFlow)
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHost)
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
                        acctLine = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier,
                                    text = account?.prettyAcct.orEmpty(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelMedium,
                                )
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
                        }
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
                        LocalSnackbarHostState provides snackbarHost,
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
            val followDescSuffix = stringResource(R.string.activity_pub_user_detail_follower_info)
            val followerDesc = remember(account) {
                if (account == null) {
                    buildAnnotatedString { append("    ") }
                } else {
                    buildAnnotatedString {
                        val followCount = account.followersCount.toString()
                        append(followCount)
                        addStyle(
                            style = SpanStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            start = 0,
                            end = followCount.length,
                        )
                        append(" ")
                        append(followDescSuffix)
                    }
                }
            }
            Text(
                modifier = Modifier.clickable(account != null) {
                    onFollowerClick()
                },
                text = followerDesc,
                style = MaterialTheme.typography.bodySmall,
            )

            Text(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                text = "·",
                style = MaterialTheme.typography.bodySmall,
            )

            val followingDescSuffix =
                stringResource(R.string.activity_pub_user_detail_following_info)
            val followingDesc = remember(account) {
                if (account == null) {
                    buildAnnotatedString { append("    ") }
                } else {
                    buildAnnotatedString {
                        val followingCount = account.followingCount.toString()
                        append(followingCount)
                        addStyle(
                            style = SpanStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                            ),
                            start = 0,
                            end = followingCount.length,
                        )
                        append(" ")
                        append(followingDescSuffix)
                    }
                }
            }
            Text(
                modifier = Modifier.clickable(account != null) {
                    onFollowingClick()
                },
                text = followingDesc,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

    @Composable
    private fun ToolbarActions(
        uiState: UserDetailUiState,
        onBlockClick: () -> Unit,
        onBlockDomainClick: () -> Unit,
        onUnblockDomainClick: () -> Unit,
        onOpenInBrowserClick: () -> Unit,
        onEditClick: () -> Unit,
    ) {
        val account = uiState.accountUiState?.account ?: return
        val userInsights = uiState.userInsight ?: return
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
        DropdownMenu(
            expanded = showMorePopup,
            onDismissRequest = { showMorePopup = false },
        ) {
            if (uiState.relationship?.blocking == false) {
                SimpleDropdownMenuItem(
                    text = stringResource(
                        R.string.activity_pub_user_detail_menu_block,
                        account.displayName.take(10)
                    ),
                    onClick = {
                        showMorePopup = false
                        showBlockUserConfirmDialog = true
                    }
                )
            }
            val domainBlocked = uiState.domainBlocked
            val host = userInsights.baseUrl.host
            if (domainBlocked != null) {
                val blockDomainLabel = if (domainBlocked) {
                    stringResource(R.string.activity_pub_user_detail_menu_unblock_domain, host)
                } else {
                    stringResource(R.string.activity_pub_user_detail_menu_block_domain, host)
                }
                SimpleDropdownMenuItem(
                    text = blockDomainLabel,
                    onClick = {
                        showMorePopup = false
                        if (domainBlocked) {
                            onUnblockDomainClick()
                        } else {
                            showBlockDomainConfirmDialog = true
                        }
                    }
                )
            }
            SimpleDropdownMenuItem(
                text = stringResource(R.string.activity_pub_user_detail_menu_open_in_browser),
                onClick = {
                    showMorePopup = false
                    onOpenInBrowserClick()
                }
            )
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
    }

    @Composable
    private fun SimpleDropdownMenuItem(
        text: String,
        onClick: () -> Unit,
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    text = text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            onClick = onClick,
        )
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
