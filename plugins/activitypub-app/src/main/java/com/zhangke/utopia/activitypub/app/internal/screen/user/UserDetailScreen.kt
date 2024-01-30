package com.zhangke.utopia.activitypub.app.internal.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.activitypub.entities.ActivityPubRelationshipEntity
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.StyledTextButton
import com.zhangke.framework.composable.TextButtonStyle
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.collapsable.CollapsableTopBarLayout
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.utopia.activitypub.app.R

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
            onBackClick = navigator::pop,
            onFollowClick = viewModel::onFollowClick,
            onUnfollowClick = viewModel::onUnfollowClick,
            onAcceptClick = viewModel::onAcceptClick,
            onRejectClick = viewModel::onRejectClick,
        )
    }

    @Composable
    private fun UserDetailContent(
        uiState: UserDetailUiState,
        onBackClick: () -> Unit,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
        onAcceptClick: () -> Unit,
        onRejectClick: () -> Unit,
    ) {
        val contentCanScrollBackward = remember {
            mutableStateOf(false)
        }
        val snackbarHost = rememberSnackbarHostState()
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHost)
            },
        ) { paddings ->
            CollapsableTopBarLayout(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(paddings),
                minTopBarHeight = ToolbarTokens.ContainerHeight,
                contentCanScrollBackward = contentCanScrollBackward,
                topBar = { collapsableProgress ->
                    UserDetailAppBar(
                        uiState = uiState,
                        collapsableProgress = collapsableProgress,
                        onBackClick = onBackClick,
                        onFollowClick = onFollowClick,
                        onUnfollowClick = onUnfollowClick,
                        onAcceptClick = onAcceptClick,
                        onRejectClick = onRejectClick,
                    )
                },
            ) {
                val list = remember {
                    with(mutableListOf<String>()) {
                        repeat(100) {
                            add("item $it")
                        }
                        this
                    }
                }
                val state = rememberLazyListState()
                contentCanScrollBackward.value = state.canScrollBackward
                LazyColumn(
                    state = state,
                ) {
                    items(list) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            Text(text = item)
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun UserDetailAppBar(
        uiState: UserDetailUiState,
        collapsableProgress: Float,
        onBackClick: () -> Unit,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
        onAcceptClick: () -> Unit,
        onRejectClick: () -> Unit,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.ArrowBack),
                            "back"
                        )
                    }
                },
                title = {
                    Text(
                        text = uiState.account?.displayName.orEmpty(),
                        fontSize = 18.sp,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(),
            )
            UserDetailHeaderInfo(
                uiState = uiState,
                collapsableProgress = collapsableProgress,
                onFollowClick = onFollowClick,
                onUnfollowClick = onUnfollowClick,
                onAcceptClick = onAcceptClick,
                onRejectClick = onRejectClick,
            )
        }
    }

    /**
     * Appbar 中不包含 Toolbar 的部分
     */
    @Composable
    private fun UserDetailHeaderInfo(
        uiState: UserDetailUiState,
        collapsableProgress: Float,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
        onAcceptClick: () -> Unit,
        onRejectClick: () -> Unit,
    ) {
        val account = uiState.account
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth(),
        ) {
            val (bannerRef, avatarRef, surfacePlaceholder, relationshipRef, nameRef, accRef, descRef, followInfoRef) = createRefs()
            // banner
            AsyncImage(
                modifier = Modifier
                    .utopiaPlaceholder(account?.header.isNullOrEmpty())
                    .constrainAs(bannerRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.value(120.dp)
                    },
                model = account?.header,
                contentScale = ContentScale.Crop,
                contentDescription = null,
            )
            // banner 下面的白色背景
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .constrainAs(surfacePlaceholder) {
                        top.linkTo(bannerRef.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )
            // avatar
            AsyncImage(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(4.dp, Color.White, CircleShape)
                    .utopiaPlaceholder(account?.avatar.isNullOrEmpty())
                    .constrainAs(avatarRef) {
                        top.linkTo(bannerRef.bottom, (-32).dp)
                        start.linkTo(parent.start, 16.dp)
                    },
                model = account?.avatar,
                contentScale = ContentScale.Crop,
                contentDescription = "avatar",
            )

            // relationship button
            RelationshipStateButton(
                modifier = Modifier.constrainAs(relationshipRef) {
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(bannerRef.bottom, 16.dp)
                },
                uiState = uiState,
                onFollowClick = onFollowClick,
                onUnfollowClick = onUnfollowClick,
                onAcceptClick = onAcceptClick,
                onRejectClick = onRejectClick,
            )

            // user name
            Text(
                modifier = Modifier
                    .utopiaPlaceholder(account?.displayName.isNullOrEmpty())
                    .constrainAs(nameRef) {
                        top.linkTo(avatarRef.bottom, 16.dp)
                        start.linkTo(avatarRef.start)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                text = account?.displayName.orEmpty(),
                fontSize = 18.sp,
            )

            // acct
            Text(
                modifier = Modifier
                    .utopiaPlaceholder(account?.acct.isNullOrEmpty())
                    .constrainAs(accRef) {
                        top.linkTo(nameRef.bottom, 4.dp)
                        start.linkTo(nameRef.start)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    },
                text = account?.acct.orEmpty(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.labelMedium,
            )

            // description
            Text(
                modifier = Modifier
                    .utopiaPlaceholder(account?.note.isNullOrEmpty())
                    .constrainAs(descRef) {
                        top.linkTo(accRef.bottom, 4.dp)
                        start.linkTo(nameRef.start)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    },
                text = account?.note.orEmpty(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyMedium,
            )

            val followersCount = formatCount(account?.followersCount ?: 0)
            val followingCount = formatCount(account?.followingCount ?: 0)
            Text(
                modifier = Modifier.constrainAs(followInfoRef) {
                    top.linkTo(descRef.bottom, 4.dp)
                    start.linkTo(nameRef.start)
                },
                text = stringResource(R.string.activity_pub_user_detail_follow_info, followersCount, followingCount),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }

    private fun formatCount(count: Int): String {
        return if (count >= 1_000_000) {
            "%.1f".format(count / 1_000_000F)
                .removeSuffix(".0")
                .plus("M")
        } else if (count >= 1000) {
            "%.1f".format(count / 1000F)
                .removeSuffix(".0")
                .plus("K")
        } else {
            count.toString()
        }
    }

    @Composable
    private fun RelationshipStateButton(
        modifier: Modifier,
        uiState: UserDetailUiState,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
        onAcceptClick: () -> Unit,
        onRejectClick: () -> Unit,
    ) {
        when (uiState.relationship.toUiState()) {
            RelationshipUiState.BLOCKING -> {
                SimpleTextButton(
                    modifier = modifier,
                    style = TextButtonStyle.ALERT,
                    text = stringResource(R.string.activity_pub_user_detail_relationship_blocking),
                    onClick = onUnfollowClick,
                )
            }

            RelationshipUiState.BLOCKED_BY -> {
                SimpleTextButton(
                    modifier = modifier,
                    style = TextButtonStyle.DISABLE,
                    text = stringResource(R.string.activity_pub_user_detail_relationship_not_follow),
                    onClick = onUnfollowClick,
                )
            }

            RelationshipUiState.FOLLOWING -> {
                SimpleTextButton(
                    modifier = modifier,
                    style = TextButtonStyle.STANDARD,
                    text = stringResource(R.string.activity_pub_user_detail_relationship_following),
                    onClick = onUnfollowClick,
                )
            }

            RelationshipUiState.FOLLOWED_BY -> {
                SimpleTextButton(
                    modifier = modifier,
                    style = TextButtonStyle.ACTIVE,
                    text = stringResource(R.string.activity_pub_user_detail_relationship_not_follow),
                    onClick = onFollowClick,
                )
            }

            RelationshipUiState.REQUESTED -> {
                SimpleTextButton(
                    modifier = modifier,
                    style = TextButtonStyle.STANDARD,
                    text = stringResource(R.string.activity_pub_user_detail_relationship_requested),
                    onClick = onFollowClick,
                )
            }

            RelationshipUiState.REQUEST_BY -> {
                FollowRequestBy(
                    modifier = modifier,
                    onAcceptClick = onAcceptClick,
                    onRejectClick = onRejectClick,
                )
            }

            RelationshipUiState.UNKNOWN -> {
                Box(modifier = modifier)
            }
        }
    }

    @Composable
    private fun SimpleTextButton(
        modifier: Modifier,
        text: String,
        style: TextButtonStyle,
        onClick: () -> Unit,
    ) {
        StyledTextButton(
            modifier = modifier,
            text = text,
            style = style,
            onClick = onClick,
        )
    }

    @Composable
    private fun FollowRequestBy(
        modifier: Modifier,
        onAcceptClick: () -> Unit,
        onRejectClick: () -> Unit,
    ) {
        Column(
            modifier = modifier
                .border(
                    width = 1.dp,
                    color = Color.Red,
                    shape = RoundedCornerShape(6.dp),
                )
                .padding(horizontal = 6.dp, vertical = 6.dp),
        ) {
            Text(
                text = stringResource(R.string.activity_pub_user_detail_request_by_tip),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
            )
            Row(
                modifier = Modifier.padding(top = 4.dp),
            ) {
                SimpleIconButton(
                    modifier = Modifier
                        .size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    onClick = onRejectClick,
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Reject",
                )

                SimpleIconButton(
                    modifier = Modifier
                        .size(32.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
                    onClick = onAcceptClick,
                    imageVector = Icons.Default.Check,
                    contentDescription = "Accept",
                )
            }
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
            else -> RelationshipUiState.UNKNOWN
        }
    }

    enum class RelationshipUiState {
        BLOCKING,
        BLOCKED_BY,
        FOLLOWING,
        FOLLOWED_BY,
        REQUESTED,
        REQUEST_BY,
        UNKNOWN,
    }
}
