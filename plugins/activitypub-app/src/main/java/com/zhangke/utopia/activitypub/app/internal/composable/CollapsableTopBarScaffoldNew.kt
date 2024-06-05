package com.zhangke.utopia.activitypub.app.internal.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
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
import com.zhangke.framework.composable.collapsable.rememberCollapsableTopBarLayoutConnection
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.framework.ktx.second
import com.zhangke.framework.utils.pxToDp
import com.zhangke.utopia.activitypub.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollUpTopBarLayout(
    modifier: Modifier = Modifier,
    topBarContent: @Composable BoxScope.(progress: Float) -> Unit,
    headerContent: @Composable () -> Unit,
    minTopBarHeight: Dp = ToolbarTokens.ContainerHeight,
    contentCanScrollBackward: State<Boolean>,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollableContent: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    val minTopBarHeightPx = with(density) { minTopBarHeight.toPx() }
    var maxTopBarHeightPx: Int by remember {
        mutableIntStateOf(0)
    }
    val nestedScrollConnection = rememberCollapsableTopBarLayoutConnection(
        contentCanScrollBackward = contentCanScrollBackward,
        maxPx = maxTopBarHeightPx.toFloat(),
        minPx = minTopBarHeightPx,
    )

    val progress by rememberUpdatedState(newValue = nestedScrollConnection.progress)
    val statusBarHeight = windowInsets.getTop(density).pxToDp(density)
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
    ) {
        Layout(
            modifier = Modifier.padding(top = statusBarHeight),
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                ) {
                    topBarContent(progress)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    scrollableContent()
                }
            },
            measurePolicy = { measurables, constraints ->
                val topBarPlaceable = measurables.first().measure(constraints)
                if (maxTopBarHeightPx != topBarPlaceable.measuredHeight) {
                    maxTopBarHeightPx = topBarPlaceable.measuredHeight
                }
                val tabBarPlaceable = measurables.second().measure(constraints)
                val scrollableContentPlaceable = measurables[2].measure(constraints)
                layout(constraints.maxWidth, constraints.maxHeight) {
                    val totalHeaderHeight = maxTopBarHeightPx
                    val processedOffset = totalHeaderHeight * progress
                    val tabBarYOffset = topBarPlaceable.measuredHeight - processedOffset.coerceIn(0F, tabHeightPx)
                    tabBarPlaceable.placeRelative(0, tabBarYOffset.toInt())
                    val topBarYOffset = -((processedOffset - tabHeightPx).coerceIn(0F, tabHeightPx))
                    topBarPlaceable.placeRelative(0, topBarYOffset.toInt())
                    scrollableContentPlaceable.placeRelative(
                        0,
                        totalHeaderHeight.toInt() - processedOffset.toInt()
                    )
                }
            },
        )
        if (statusBarHeight > 0.dp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(statusBarHeight)
                    .background(colors.containerColor)
            )
        }
    }
}

@Composable
private fun rememberCollapsableTopBarLayoutConnection(
    contentCanScrollBackward: State<Boolean>,
    maxPx: Float?,
    minPx: Float,
): ICollapsableTopBarLayoutConnection {
    return if (maxPx == null) {
        remember {
            StaticTopBarLayoutConnection()
        }
    } else {
        remember(contentCanScrollBackward, maxPx, minPx) {
            CollapsableTopBarLayoutConnection(contentCanScrollBackward, maxPx, minPx)
        }
    }
}

interface ICollapsableTopBarLayoutConnection : NestedScrollConnection {

    val progress: Float
}

class StaticTopBarLayoutConnection : NestedScrollConnection, ICollapsableTopBarLayoutConnection {

    override val progress: Float = 0F
}

private class CollapsableTopBarLayoutConnection(
    private val contentCanScrollBackward: State<Boolean>,
    private val maxPx: Float,
    private val minPx: Float,
) : NestedScrollConnection, ICollapsableTopBarLayoutConnection {

    private var topBarHeight: Float = maxPx
        set(value) {
            field = value
            progress = 1 - (topBarHeight - minPx) / (maxPx - minPx)
        }

    override var progress: Float by mutableFloatStateOf(0F)
        private set

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val height = topBarHeight

        if (height == minPx) {
            if (available.y > 0F) {
                return if (contentCanScrollBackward.value) {
                    Offset.Zero
                } else {
                    topBarHeight += available.y
                    Offset(0F, available.y)
                }
            }
        }

        if (height + available.y > maxPx) {
            topBarHeight = maxPx
            return Offset(0f, maxPx - height)
        }

        if (height + available.y < minPx) {
            topBarHeight = minPx
            return Offset(0f, minPx - height)
        }

        topBarHeight += available.y

        return Offset(0f, available.y)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollUpTopBarLayoutOld(
    modifier: Modifier = Modifier,
    contentCanScrollBackward: State<Boolean>,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    topSection: @Composable (progress: Float) -> Unit,
    content: @Composable () -> Unit,
) {
    val bannerHeight = 180.dp
    val avatarSize = 80.dp
    val topBarHeight = ToolbarTokens.ContainerHeight
    val density = LocalDensity.current
    val minTopBarHeightPx = with(density) { topBarHeight.toPx() }
    var maxTopBarHeightPx: Float? by remember {
        mutableStateOf(null)
    }
    var progress: Float by remember {
        mutableFloatStateOf(0F)
    }
    val finalModifier = if (maxTopBarHeightPx == null) {
        Modifier.then(modifier)
    } else {
        val connection = rememberCollapsableTopBarLayoutConnection(
            contentCanScrollBackward = contentCanScrollBackward,
            maxPx = maxTopBarHeightPx!!,
            minPx = minTopBarHeightPx,
        )
        progress = connection.progress
        Modifier
            .then(modifier)
            .nestedScroll(connection)
    }

    Column(modifier = finalModifier) {
        Box(
            modifier = Modifier
                .scrollable(rememberScrollState(), Orientation.Vertical)
                .onGloballyPositioned {
                    if (maxTopBarHeightPx == null || maxTopBarHeightPx == 0F) {
                        maxTopBarHeightPx = it.size.height.toFloat()
                    }
                }
        ) {
            topSection(progress)
        }
        Box(
            modifier = Modifier.scrollable(rememberScrollState(), Orientation.Vertical)
        ) {
            content()
        }
    }
}

/**
 * 包含：
 * - toolbar
 * - banner
 * - 头像
 * - header 内容
 */
@OptIn(ExperimentalMotionApi::class)
@Composable
fun CollapsableTopBarScaffoldNew(
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
    val bannerHeight = 180.dp
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
