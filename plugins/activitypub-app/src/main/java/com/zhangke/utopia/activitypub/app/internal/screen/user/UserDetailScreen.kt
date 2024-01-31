package com.zhangke.utopia.activitypub.app.internal.screen.user

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.HorizontalPagerWithTab
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.text.RichText
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.framework.utils.formatAsCount
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.composable.CollapsableTopBarScaffold
import com.zhangke.utopia.activitypub.app.internal.screen.user.about.UserAboutTab
import com.zhangke.utopia.activitypub.app.internal.screen.user.timeline.UserTimelineTab
import kotlinx.coroutines.flow.SharedFlow

@Destination(UserDetailRoute.ROUTE)
class UserDetailScreen(
    @Router val route: String = "",
) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<UserDetailViewModel, UserDetailViewModel.Factory> {
            it.create(UserDetailRoute.parseRoute(route))
        }
        val uiState by viewModel.uiState.collectAsState()
        UserDetailContent(
            uiState = uiState,
            messageFlow = viewModel.messageFlow,
            onBackClick = navigator::pop,
            onFollowClick = viewModel::onFollowClick,
            onUnfollowClick = viewModel::onUnfollowClick,
            onAcceptClick = viewModel::onAcceptClick,
            onRejectClick = viewModel::onRejectClick,
            onCancelFollowRequestClick = viewModel::onCancelFollowRequestClick,
            onUnblockClick = viewModel::onUnblockClick,
        )
    }

    @Composable
    private fun UserDetailContent(
        uiState: UserDetailUiState,
        messageFlow: SharedFlow<TextString>,
        onBackClick: () -> Unit,
        onUnblockClick: () -> Unit,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
        onCancelFollowRequestClick: () -> Unit,
        onAcceptClick: () -> Unit,
        onRejectClick: () -> Unit,
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
        ) { paddings ->
            val account = uiState.account
            CollapsableTopBarScaffold(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(paddings),
                title = account?.displayName,
                banner = account?.header,
                avatar = account?.avatar,
                contentCanScrollBackward = contentCanScrollBackward,
                onBackClick = onBackClick,
                toolbarAction = {},
                headerAction = {
                    RelationshipStateButton(
                        modifier = Modifier,
                        uiState = uiState,
                        onFollowClick = onFollowClick,
                        onUnfollowClick = onUnfollowClick,
                        onAcceptClick = onAcceptClick,
                        onRejectClick = onRejectClick,
                        onCancelFollowRequestClick = onCancelFollowRequestClick,
                        onUnblockClick = onUnblockClick,
                    )
                },
                headerContent = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // acct
                        val acct = remember {
                            val acct = account?.acct.orEmpty()
                            if (acct.isNotEmpty() && !acct.contains('@')) {
                                "@$acct"
                            } else {
                                acct
                            }
                        }
                        Text(
                            modifier = Modifier
                                .utopiaPlaceholder(account?.acct.isNullOrEmpty()),
                            text = acct,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Start,
                            style = MaterialTheme.typography.labelMedium,
                        )

                        // description
                        RichText(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .utopiaPlaceholder(account?.note.isNullOrEmpty())
                                .fillMaxWidth(),
                            text = account?.note.orEmpty(),
                        )

                        val followInfo = if (account == null) {
                            ""
                        } else {
                            val followersCount = account.followersCount.formatAsCount()
                            val followingCount = account.followingCount.formatAsCount()
                            stringResource(
                                R.string.activity_pub_user_detail_follow_info,
                                followersCount,
                                followingCount,
                            )
                        }
                        Text(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .utopiaPlaceholder(followInfo.isEmpty())
                                .fillMaxWidth(),
                            text = followInfo,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                },
            ) {
                if (uiState.userInsight != null) {
                    val tabs: List<PagerTab> = remember {
                        listOf(
                            UserTimelineTab(contentCanScrollBackward, uiState.userInsight),
                            UserAboutTab(contentCanScrollBackward, uiState.userInsight),
                        )
                    }
                    CompositionLocalProvider(
                        LocalSnackbarHostState provides snackbarHost
                    ) {
                        HorizontalPagerWithTab(tabList = tabs)
                    }
                }
            }
        }
    }
}
