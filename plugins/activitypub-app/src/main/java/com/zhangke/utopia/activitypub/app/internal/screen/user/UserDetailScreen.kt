package com.zhangke.utopia.activitypub.app.internal.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionScene
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.text.RichText
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.framework.utils.formatAsCount
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.composable.CollapsableTopBarScaffold
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
        )
    }


    @OptIn(ExperimentalMotionApi::class)
    @Composable
    private fun UserDetailContent(
        uiState: UserDetailUiState,
        messageFlow: SharedFlow<TextString>,
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
                    )
                },
                headerContent = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // acct
                        Text(
                            modifier = Modifier
                                .utopiaPlaceholder(account?.acct.isNullOrEmpty()),
                            text = account?.acct.orEmpty(),
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
                val list = remember {
                    with(mutableListOf<String>()) {
                        repeat(100) {
                            add("item $it")
                        }
                        this
                    }
                }
                val listState = rememberLazyListState()
                val canScrollBackward by remember {
                    derivedStateOf {
                        listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
                    }
                }
                contentCanScrollBackward.value = canScrollBackward
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
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

//
//    @OptIn(ExperimentalMotionApi::class)
//    @Composable
//    private fun UserDetailContent(
//        uiState: UserDetailUiState,
//        messageFlow: SharedFlow<TextString>,
//        onBackClick: () -> Unit,
//        onFollowClick: () -> Unit,
//        onUnfollowClick: () -> Unit,
//        onAcceptClick: () -> Unit,
//        onRejectClick: () -> Unit,
//    ) {
//        val contentCanScrollBackward = remember {
//            mutableStateOf(false)
//        }
//        val snackbarHost = rememberSnackbarHostState()
//        ConsumeSnackbarFlow(hostState = snackbarHost, messageTextFlow = messageFlow)
//        Scaffold(
//            snackbarHost = {
//                SnackbarHost(hostState = snackbarHost)
//            },
//        ) { paddings ->
//            CollapsableTopBarLayout(
//                modifier = Modifier
//                    .statusBarsPadding()
//                    .padding(paddings),
//                minTopBarHeight = ToolbarTokens.ContainerHeight,
//                contentCanScrollBackward = contentCanScrollBackward,
//                topBar = { collapsableProgress ->
//                    MotionLayout(
//                        modifier = Modifier.fillMaxWidth(),
//                        motionScene = buildMotionScene(),
//                        progress = collapsableProgress,
//                    ) {
//                        UserDetailAppBar(
//                            uiState = uiState,
//                            collapsableProgress = collapsableProgress,
//                            onBackClick = onBackClick,
//                            onFollowClick = onFollowClick,
//                            onUnfollowClick = onUnfollowClick,
//                            onAcceptClick = onAcceptClick,
//                            onRejectClick = onRejectClick,
//                        )
//                    }
//                },
//            ) {
//                val list = remember {
//                    with(mutableListOf<String>()) {
//                        repeat(100) {
//                            add("item $it")
//                        }
//                        this
//                    }
//                }
//                val listState = rememberLazyListState()
//                val canScrollBackward by remember {
//                    derivedStateOf {
//                        listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
//                    }
//                }
//                contentCanScrollBackward.value = canScrollBackward
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    state = listState,
//                ) {
//                    items(list) { item ->
//                        Card(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(vertical = 8.dp, horizontal = 16.dp)
//                        ) {
//                            Text(text = item)
//                        }
//                    }
//                }
//            }
//        }
//    }

    @OptIn(ExperimentalMotionApi::class)
    @Composable
    private fun buildMotionScene() = MotionScene {
        val toolbar = createRefFor("toolbar")
        val userInfoHeader = createRefFor("userInfoHeader")
        val start1 = constraintSet {
            constrain(toolbar) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
            constrain(userInfoHeader) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        }

        val end1 = constraintSet {
            constrain(toolbar) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
            }
            constrain(userInfoHeader) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(toolbar.bottom)
                width = Dimension.fillToConstraints
            }
        }
        transition("default", start1, end1) {}
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
        UserDetailHeaderInfo(
            modifier = Modifier.layoutId("userInfoHeader"),
            uiState = uiState,
            collapsableProgress = collapsableProgress,
            onFollowClick = onFollowClick,
            onUnfollowClick = onUnfollowClick,
            onAcceptClick = onAcceptClick,
            onRejectClick = onRejectClick,
        )
        val toolbarColors = TopAppBarDefaults.topAppBarColors()
        TopAppBar(
            modifier = Modifier.layoutId("toolbar"),
            colors = toolbarColors.copy(
                containerColor = toolbarColors.containerColor.copy(alpha = collapsableProgress)
            ),
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        )
    }

    /**
     * Appbar 中不包含 Toolbar 的部分
     */
    @Composable
    private fun UserDetailHeaderInfo(
        modifier: Modifier,
        uiState: UserDetailUiState,
        collapsableProgress: Float,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
        onAcceptClick: () -> Unit,
        onRejectClick: () -> Unit,
    ) {
        val account = uiState.account
        ConstraintLayout(
            modifier = modifier.fillMaxWidth(),
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
                    .size(68.dp)
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
                        top.linkTo(avatarRef.bottom, 8.dp)
                        start.linkTo(avatarRef.start)
                        end.linkTo(relationshipRef.start, 8.dp)
                        width = Dimension.fillToConstraints
                    },
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
            RichText(
                modifier = Modifier
                    .utopiaPlaceholder(account?.note.isNullOrEmpty())
                    .constrainAs(descRef) {
                        top.linkTo(accRef.bottom, 4.dp)
                        start.linkTo(nameRef.start)
                        end.linkTo(parent.end, 16.dp)
                        width = Dimension.fillToConstraints
                    },
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
                    .utopiaPlaceholder(followInfo.isEmpty())
                    .constrainAs(followInfoRef) {
                        top.linkTo(descRef.bottom, 4.dp)
                        start.linkTo(nameRef.start)
                    },
                text = followInfo,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
