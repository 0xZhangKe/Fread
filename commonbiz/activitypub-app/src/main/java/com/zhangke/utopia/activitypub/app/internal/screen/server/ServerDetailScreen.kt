package com.zhangke.utopia.activitypub.app.internal.screen.server

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.collapsable.CollapsableTopBarLayout
import com.zhangke.framework.composable.textString
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.utopia.activitypub.app.internal.uri.server.ActivityPubServerUri
import kotlinx.coroutines.launch

@Destination(ActivityPubServerUri.baseUrl)
class ServerDetailScreen(
    @Router val router: String = "",
) : AndroidScreen() {

    @Composable
    override fun Content() {
        val viewModel: ServerDetailViewModel = getViewModel()
        viewModel.uri = router
        LaunchedEffect(Unit) {
            viewModel.onPageResume()
        }
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        ServiceDetailContent(
            uiState = uiState,
            onBackClick = navigator::pop,
        )
    }

    @OptIn(ExperimentalMotionApi::class, ExperimentalFoundationApi::class)
    @Composable
    private fun ServiceDetailContent(
        uiState: ServerDetailUiState,
        onBackClick: () -> Unit,
    ) {
        val coroutineScope = rememberCoroutineScope()
        val toolbarHeight = ToolbarTokens.ContainerHeight
        val motionScene = MotionScene {
            val backIcon = createRefFor("backIcon")
            val toolbarPlaceholder = createRefFor("toolbarPlaceholder")
            val banner = createRefFor("banner")
            val content = createRefFor("content")
            val avatar = createRefFor("avatar")
            val toolbarTitle = createRefFor("toolbarTitle")
            val bannerHeight = 120.dp
            val start1 = constraintSet {
                constrain(backIcon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    customColor("color", Color(0xffffffff))
                }
                constrain(toolbarPlaceholder) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    alpha = 0F
                }
                constrain(banner) {
                    width = Dimension.fillToConstraints
                    height = Dimension.value(bannerHeight)
                    top.linkTo(parent.top)
                }
                constrain(content) {
                    width = Dimension.fillToConstraints
                    top.linkTo(parent.top, bannerHeight)
                }
                constrain(avatar) {
                    start.linkTo(parent.start, 10.dp)
                    top.linkTo(content.top)
                    bottom.linkTo(content.top)
                    width = Dimension.value(64.dp)
                    height = Dimension.value(64.dp)
                }
                constrain(toolbarTitle) {
                    start.linkTo(backIcon.end, 16.dp)
                    top.linkTo(toolbarPlaceholder.top)
                    bottom.linkTo(toolbarPlaceholder.bottom)
                    customFloat("visible", 0F)
                }
            }
            val end1 = constraintSet {
                constrain(backIcon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    customColor("color", Color(0xFF000000))
                }
                constrain(toolbarPlaceholder) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    alpha = 1F
                }
                constrain(banner) {
                    width = Dimension.matchParent
                    height = Dimension.value(bannerHeight)
                    top.linkTo(parent.top, toolbarHeight - bannerHeight)
                }
                constrain(content) {
                    width = Dimension.fillToConstraints
                    bottom.linkTo(toolbarPlaceholder.bottom)
                }
                constrain(avatar) {
                    start.linkTo(parent.start, 42.dp)
                    top.linkTo(content.top)
                    bottom.linkTo(content.top)
                    width = Dimension.value(0.dp)
                    height = Dimension.value(0.dp)
                }
                constrain(toolbarTitle) {
                    start.linkTo(backIcon.end, 16.dp)
                    top.linkTo(toolbarPlaceholder.top)
                    bottom.linkTo(toolbarPlaceholder.bottom)
                    customFloat("visible", 1F)
                }
            }
            transition("default", start1, end1) {}
        }

        val contentCanScrollBackward = remember {
            mutableStateOf(false)
        }
        CollapsableTopBarLayout(
            minTopBarHeight = toolbarHeight,
            contentCanScrollBackward = contentCanScrollBackward,
            topBar = { collapsableProgress ->
                MotionLayout(
                    modifier = Modifier.fillMaxWidth(),
                    motionScene = motionScene,
                    progress = collapsableProgress,
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .layoutId("banner")
                            .utopiaPlaceholder(uiState.loading),
                        model = uiState.thumbnail,
                        contentScale = ContentScale.Crop,
                        contentDescription = "Thumbnail",
                    )
                    Surface(modifier = Modifier.layoutId("content")) {
                        Column(
                            modifier = Modifier.padding(
                                start = 10.dp,
                                top = 10.dp,
                                end = 10.dp,
                                bottom = 10.dp,
                            )
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(top = 40.dp)
                                    .utopiaPlaceholder(visible = uiState.loading),
                                text = uiState.title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .utopiaPlaceholder(visible = uiState.loading),
                                text = uiState.domain,
                                fontSize = 12.sp,
                            )
                            Text(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .utopiaPlaceholder(visible = uiState.loading),
                                text = uiState.description,
                                maxLines = 4,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 14.sp,
                            )
                            val languageString = uiState.languages.joinToString(", ")
                            Text(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .utopiaPlaceholder(visible = uiState.loading),
                                text = "语言：$languageString",
                                maxLines = 3,
                                fontSize = 14.sp,
                            )
                            Text(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .utopiaPlaceholder(visible = uiState.loading),
                                text = "月活：${uiState.activeMonth}",
                                fontSize = 14.sp,
                            )

                            if (uiState.contract != null) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .utopiaPlaceholder(visible = uiState.loading),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        modifier = Modifier
                                            .background(
                                                Color(0x6644429F),
                                                RoundedCornerShape(3.dp),
                                            )
                                            .padding(vertical = 2.dp, horizontal = 4.dp),
                                        text = "MOD",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                    )

                                    AsyncImage(
                                        modifier = Modifier
                                            .padding(start = 6.dp)
                                            .size(18.dp)
                                            .clip(CircleShape),
                                        model = uiState.contract.account.avatar,
                                        contentDescription = "Mod avatar",
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 4.dp),
                                        text = uiState.contract.account.displayName,
                                        fontSize = 14.sp,
                                    )
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 4.dp),
                                        text = uiState.contract.email,
                                        fontSize = 14.sp,
                                    )
                                }
                            }
                        }
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(toolbarHeight)
                            .background(Color.White)
                            .layoutId("toolbarPlaceholder"),
                        shadowElevation = 2.dp,
                    ) {}
                    AsyncImage(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .layoutId("avatar")
                            .utopiaPlaceholder(visible = uiState.loading),
                        model = uiState.thumbnail,
                        contentScale = ContentScale.Crop,
                        contentDescription = "avatar",
                    )
                    val backIconProperties = motionProperties(id = "backIcon")
                    Box(
                        modifier = Modifier
                            .height(ToolbarTokens.ContainerHeight)
                            .padding(start = ToolbarTokens.TopAppBarHorizontalPadding)
                            .layoutId("backIcon"),
                        contentAlignment = Alignment.Center,
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                modifier = Modifier.size(ToolbarTokens.LeadingIconSize),
                                painter = rememberVectorPainter(Icons.Default.ArrowBack),
                                contentDescription = "back",
                                tint = backIconProperties.value.color("color"),
                            )
                        }
                    }
                    val toolbarTitleProperties = motionProperties(id = "toolbarTitle")
                    val toolbarTitleVisible = toolbarTitleProperties.value.float("visible")
                    Text(
                        modifier = Modifier.layoutId("toolbarTitle"),
                        text = uiState.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = if (toolbarTitleVisible >= 1F) 1F else 0F),
                    )
                }
            }
        ) {
            Column {
                val tabs = uiState.tabs
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = tabs::size,
                )
                TabRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    selectedTabIndex = pagerState.currentPage,
                ) {
                    tabs.forEachIndexed { index, item ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                        ) {
                            Box(
                                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            ) {
                                Text(
                                    text = textString(item.title),
                                )
                            }
                        }
                    }
                }
                HorizontalPager(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F),
                    state = pagerState,
                ) { currentPage ->
                    // TODO check this, why need Screen params but receiver
                    val host = uiState.domain
                    if (host.isNotEmpty()) {
                        tabs[currentPage].content(
                            this@ServerDetailScreen,
                            uiState.domain,
                            uiState.rules,
                            contentCanScrollBackward,
                        )
                    }
                }
            }
        }
    }
}
