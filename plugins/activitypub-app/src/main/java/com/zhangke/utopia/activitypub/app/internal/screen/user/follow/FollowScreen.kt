package com.zhangke.utopia.activitypub.app.internal.screen.user.follow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.screen.user.UserDetailRoute
import com.zhangke.utopia.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.ui.BlogAuthorAvatar
import com.zhangke.utopia.status.ui.richtext.UtopiaRichText
import com.zhangke.utopia.status.uri.FormalUri

class FollowScreen(
    private val role: IdentityRole,
    private val userUri: FormalUri,
    private val isFollowing: Boolean,
) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<FollowViewModel, FollowViewModel.Factory> {
            it.create(role, userUri, isFollowing)
        }
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = rememberSnackbarHostState()
        FollowContent(
            uiState = uiState,
            onBackClick = navigator::pop,
            snackbarHostState = snackbarHostState,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onAccountClick = {
                val route = UserDetailRoute.buildRoute(role, it.uri)
                navigator.push(UserDetailScreen(route))
            },
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.messageFlow)
    }

    @Composable
    private fun FollowContent(
        uiState: FollowUiState,
        onBackClick: () -> Unit,
        snackbarHostState: SnackbarHostState,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onAccountClick: (BlogAuthor) -> Unit,
    ) {
        Scaffold(
            topBar = {
                val title = if (isFollowing) {
                    stringResource(R.string.activity_pub_user_following_list_title)
                } else {
                    stringResource(R.string.activity_pub_user_follower_list_title)
                }
                Toolbar(
                    title = title,
                    onBackClick = onBackClick,
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            }
        ) { paddings ->
            if (uiState.initializing) {
                InitializingUi(Modifier.padding(paddings))
            } else {
                if (uiState.list.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "No users were obtained",
                        )
                    }
                } else {
                    val state = rememberLoadableLazyColumnState(
                        refreshing = uiState.refreshing,
                        onRefresh = onRefresh,
                        onLoadMore = onLoadMore,
                    )
                    LoadableLazyColumn(
                        modifier = Modifier
                            .padding(paddings)
                            .fillMaxSize(),
                        state = state,
                        refreshing = uiState.refreshing,
                        loadState = uiState.loadMoreState,
                    ) {
                        items(uiState.list) { account ->
                            FollowAccountUi(
                                account = account,
                                onClick = onAccountClick,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun InitializingUi(modifier: Modifier) {
        Column(modifier = modifier.fillMaxSize()) {
            repeat(20) {
                FollowAccountUi(null, {})
            }
        }
    }

    @Composable
    private fun FollowAccountUi(
        account: BlogAuthor?,
        onClick: (BlogAuthor) -> Unit,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (account != null) {
                        onClick(account)
                    }
                },
        ) {
            val (avatarRef, nameRef, acctRef, descRef, dividerRef) = createRefs()
            BlogAuthorAvatar(
                modifier = Modifier.constrainAs(avatarRef) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(parent.top, 8.dp)
                    width = Dimension.value(38.dp)
                    height = Dimension.value(38.dp)
                },
                imageUrl = account?.avatar,
            )
            Text(
                modifier = Modifier
                    .utopiaPlaceholder(account == null)
                    .constrainAs(nameRef) {
                        start.linkTo(avatarRef.end, 4.dp)
                        top.linkTo(avatarRef.top)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    },
                textAlign = TextAlign.Start,
                text = account?.name.orEmpty(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                modifier = Modifier
                    .utopiaPlaceholder(account == null)
                    .constrainAs(acctRef) {
                        start.linkTo(nameRef.start)
                        top.linkTo(nameRef.bottom, 2.dp)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    },
                maxLines = 1,
                textAlign = TextAlign.Start,
                text = "@${account?.webFinger}",
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium,
            )
            UtopiaRichText(
                modifier = Modifier
                    .utopiaPlaceholder(account == null)
                    .constrainAs(descRef) {
                        start.linkTo(nameRef.start)
                        top.linkTo(acctRef.bottom, 2.dp)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    },
                content = account?.description.orEmpty(),
                onMentionClick = {},
                onHashtagClick = {},
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                mentions = emptyList(),
                tags = emptyList(),
                emojis = account?.emojis.orEmpty(),
            )
            HorizontalDivider(
                modifier = Modifier.constrainAs(dividerRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(descRef.bottom, 6.dp)
                    width = Dimension.fillToConstraints
                },
                thickness = if (account == null) {
                    0.dp
                } else {
                    1.dp
                }
            )
        }
    }
}
