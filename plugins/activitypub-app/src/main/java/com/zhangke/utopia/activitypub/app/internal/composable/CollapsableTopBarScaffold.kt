package com.zhangke.utopia.activitypub.app.internal.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionLayoutScope
import androidx.constraintlayout.compose.MotionScene
import coil.compose.AsyncImage
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.collapsable.CollapsableTopBarLayout
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.utopia.activitypub.app.R

/**
 * 包含：
 * - toolbar
 * - banner
 * - 头像
 * - header 内容
 */
@Composable
fun CollapsableTopBarScaffold(
    modifier: Modifier,
    title: String?,
    banner: String?,
    avatar: String?,
    contentCanScrollBackward: State<Boolean>,
    onBackClick: () -> Unit,
    onAvatarClick: () -> Unit,
    toolbarAction: @Composable RowScope.(Color) -> Unit,
    headerAction: @Composable () -> Unit,
    headerContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val bannerHeight = 150.dp
    val avatarSize = 80.dp
    CollapsableTopBarLayout(
        modifier = modifier,
        minTopBarHeight = ToolbarTokens.ContainerHeight,
        contentCanScrollBackward = contentCanScrollBackward,
        topBar = { collapsableProgress ->
            MotionLayout(
                modifier = Modifier.fillMaxWidth(),
                motionScene = buildMotionScene(
                    bannerHeight = bannerHeight,
                    avatarSize = avatarSize,
                ),
                progress = collapsableProgress,
            ) {
                MotionAppBar(
                    title = title,
                    banner = banner,
                    avatar = avatar,
                    onBackClick = onBackClick,
                    onAvatarClick = onAvatarClick,
                    toolbarAction = toolbarAction,
                    headerAction = headerAction,
                    headerContent = headerContent,
                )
            }
        },
        scrollableContent = content,
    )
}

@OptIn(ExperimentalMotionApi::class)
@Composable
private fun MotionLayoutScope.MotionAppBar(
    title: String?,
    banner: String?,
    avatar: String?,
    onBackClick: () -> Unit,
    onAvatarClick: () -> Unit,
    toolbarAction: @Composable RowScope.(Color) -> Unit,
    headerAction: @Composable () -> Unit,
    headerContent: @Composable () -> Unit,
) {
    // banner
    AsyncImage(
        modifier = Modifier
            .layoutId("banner")
            .clickable { onAvatarClick() },
        model = banner,
        contentScale = ContentScale.Crop,
        error = painterResource(R.drawable.detail_page_banner_background),
        placeholder = painterResource(R.drawable.detail_page_banner_background),
        contentDescription = null,
    )
    // avatar
    AsyncImage(
        modifier = Modifier
            .clip(CircleShape)
            .border(2.dp, Color.White, CircleShape)
            .layoutId("avatar")
            .utopiaPlaceholder(avatar.isNullOrEmpty()),
        model = avatar,
        contentScale = ContentScale.Crop,
        contentDescription = "avatar",
    )
    // header action
    Box(modifier = Modifier.layoutId("headerAction")) {
        headerAction()
    }
    // header content
    Box(modifier = Modifier.layoutId("headerContent")) {
        headerContent()
    }
    // toolbar background
    Surface(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .layoutId("toolbarBackground")
    ) {
    }
    val backIconProperties = motionProperties(id = "backIcon")
    val toolbarFontColor = backIconProperties.value.color("color")
    // toolbar action
    Row(
        modifier = Modifier.layoutId("toolbarAction"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        toolbarAction(toolbarFontColor)
    }
    // title
    Text(
        modifier = Modifier
            .layoutId("title")
            .utopiaPlaceholder(title.isNullOrEmpty()),
        textAlign = TextAlign.Start,
        text = title.orEmpty(),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontSize = 18.sp,
    )
    // back icon
    Toolbar.BackButton(
        modifier = Modifier.layoutId("backIcon"),
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMotionApi::class)
@Composable
private fun buildMotionScene(
    bannerHeight: Dp,
    avatarSize: Dp,
) = MotionScene {
    val backIcon = createRefFor("backIcon")
    val toolbarBackground = createRefFor("toolbarBackground")
    val avatar = createRefFor("avatar")
    val banner = createRefFor("banner")
    val title = createRefFor("title")
    val toolbarAction = createRefFor("toolbarAction")
    val headerAction = createRefFor("headerAction")
    val headerContent = createRefFor("headerContent")
    val start1 = constraintSet {
        constrain(backIcon) {
            start.linkTo(toolbarBackground.start, 16.dp)
            top.linkTo(toolbarBackground.top)
            bottom.linkTo(toolbarBackground.bottom)
            customColor("color", Color(0xFFFFFFFF))
        }
        constrain(toolbarBackground) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            width = Dimension.fillToConstraints
            height = Dimension.value(ToolbarTokens.ContainerHeight)
            alpha = 0F
        }
        constrain(toolbarAction) {
            end.linkTo(parent.end, 16.dp)
            top.linkTo(toolbarBackground.top)
            bottom.linkTo(toolbarBackground.bottom)
        }
        constrain(banner) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
            height = Dimension.value(bannerHeight)
        }
        constrain(avatar) {
            start.linkTo(parent.start, 16.dp)
            top.linkTo(banner.top, bannerHeight - avatarSize / 2)
            width = Dimension.value(avatarSize)
            height = Dimension.value(avatarSize)
        }
        constrain(title) {
            top.linkTo(avatar.bottom, 16.dp)
            start.linkTo(parent.start, 16.dp)
            end.linkTo(parent.end, 16.dp)
            width = Dimension.fillToConstraints
        }
        constrain(headerAction) {
            top.linkTo(banner.bottom, 8.dp)
            end.linkTo(parent.end, 16.dp)
            alpha = 1F
        }
        constrain(headerContent) {
            top.linkTo(title.bottom, 4.dp)
            start.linkTo(parent.start, 16.dp)
            end.linkTo(parent.end, 16.dp)
            width = Dimension.fillToConstraints
            alpha = 1F
        }
    }

    val end1 = constraintSet {
        constrain(backIcon) {
            start.linkTo(toolbarBackground.start, 16.dp)
            top.linkTo(toolbarBackground.top)
            bottom.linkTo(toolbarBackground.bottom)
            customColor("color", Color(0xFF000000))
        }
        constrain(toolbarBackground) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            top.linkTo(parent.top)
            width = Dimension.fillToConstraints
            height = Dimension.value(ToolbarTokens.ContainerHeight)
            alpha = 1F
        }
        constrain(toolbarAction) {
            end.linkTo(parent.end, 16.dp)
            top.linkTo(toolbarBackground.top)
            bottom.linkTo(toolbarBackground.bottom)
        }
        constrain(banner) {
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(toolbarBackground.bottom, 8.dp)
            width = Dimension.fillToConstraints
            height = Dimension.value(bannerHeight)
        }
        constrain(avatar) {
            start.linkTo(parent.start, 16.dp)
            top.linkTo(banner.top, bannerHeight - avatarSize / 2)
            width = Dimension.value(10.dp)
            height = Dimension.value(10.dp)
        }
        constrain(title) {
            top.linkTo(toolbarBackground.top)
            bottom.linkTo(toolbarBackground.bottom)
            start.linkTo(backIcon.end, 8.dp)
            end.linkTo(toolbarAction.start, 16.dp)
            width = Dimension.fillToConstraints
        }
        constrain(headerAction) {
            bottom.linkTo(toolbarBackground.bottom, 8.dp)
            end.linkTo(parent.end, 16.dp)
            alpha = 0F
        }
        constrain(headerContent) {
            bottom.linkTo(backIcon.bottom)
            start.linkTo(parent.start, 16.dp)
            end.linkTo(parent.end, 16.dp)
            width = Dimension.fillToConstraints
            alpha = 0F
        }
    }
    transition(name = "default", from = start1, to = end1) {}
}
