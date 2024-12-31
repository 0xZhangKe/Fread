package com.zhangke.fread.bluesky.internal.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.zhangke.framework.composable.collapsable.ScrollUpTopBarLayout
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.fread.bluesky.internal.composable.DetailTopBar
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.common.ProgressedAvatar
import com.zhangke.fread.status.ui.common.ProgressedBanner
import com.zhangke.fread.status.ui.common.RelationshipStateButton
import com.zhangke.fread.status.ui.common.RelationshipUiState
import com.zhangke.fread.status.ui.common.UserFollowLine
import com.zhangke.fread.statusui.Res
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
        val viewModel = getViewModel<BskyUserDetailViewModel, BskyUserDetailViewModel.Factory> {
            it.create(role, did)
        }
        val uiState by viewModel.uiState.collectAsState()
        UserDetailContent(
            uiState = uiState,
            onBackClick = navigator::pop,
            onBannerClick = {
                openFullImageScreen(transparentNavigator, uiState.banner)
            },
            onAvatarClick = {
                openFullImageScreen(transparentNavigator, uiState.avatar)
            },
            onFollowClick = viewModel::onFollowClick,
            onUnblockClick = viewModel::onUnblockClick,
            onUnfollowClick = viewModel::onUnfollowClick,
            onFollowerClick = {},
            onFollowingClick = {},
        )
    }

    @Composable
    private fun UserDetailContent(
        uiState: BskyUserDetailUiState,
        onBackClick: () -> Unit,
        onBannerClick: () -> Unit,
        onAvatarClick: () -> Unit,
        onFollowClick: () -> Unit,
        onUnfollowClick: () -> Unit,
        onUnblockClick: () -> Unit,
        onFollowerClick: () -> Unit,
        onFollowingClick: () -> Unit,
    ) {
        val contentCanScrollBackward = remember {
            mutableStateOf(false)
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                        actions = {},
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(100) {
                        Text("Item $it", modifier = Modifier.padding(16.dp))
                    }
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
            val (desRef, followRef, moreRef) = createRefs()
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
                text = stringResource(Res.string.status_ui_user_detail_follows_you),
                style = MaterialTheme.typography.bodySmall,
            )
        }
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
