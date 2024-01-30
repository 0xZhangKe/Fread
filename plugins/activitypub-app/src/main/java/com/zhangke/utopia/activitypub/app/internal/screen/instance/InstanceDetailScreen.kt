package com.zhangke.utopia.activitypub.app.internal.screen.instance

import android.util.Log
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.MotionScene
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.ToolbarTokens
import com.zhangke.framework.composable.UtopiaTabRow
import com.zhangke.framework.composable.collapsable.CollapsableTopBarLayout
import com.zhangke.framework.composable.textString
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.commonbiz.shared.screen.login.LoginBottomSheetScreen
import kotlinx.coroutines.launch

@Destination(PlatformDetailRoute.ROUTE)
class InstanceDetailScreen(
    @Router private val route: String = "",
) : Screen {

    @Composable
    override fun Content() {
        Log.d("U_TEST", "InstanceDetailScreen key is $key")
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetDialogNavigator = LocalBottomSheetNavigator.current
        val navigationResult = navigator.navigationResult
        val viewModel: InstanceDetailViewModel = getViewModel()
        val uiState by viewModel.uiState.collectAsState()
        LaunchedEffect(route) {
            val (baseUrl, addable) = PlatformDetailRoute.parseParams(route)
            viewModel.serverBaseUrl = baseUrl
            viewModel.addable = addable
            viewModel.onPrepared()
        }
        InstanceDetailContent(
            uiState = uiState,
            onBackClick = {
                if (!navigator.pop()) {
                    navigationResult.popWithResult(false)
                }
            },
            onAddClick = viewModel::onAddClick,
        )
        ConsumeFlow(viewModel.contentConfigFlow) {
            navigationResult.popWithResult(true)
        }
        ConsumeFlow(viewModel.openLoginFlow) {
            bottomSheetDialogNavigator.show(LoginBottomSheetScreen(it))
        }
    }

    @OptIn(ExperimentalMotionApi::class, ExperimentalFoundationApi::class)
    @Composable
    private fun InstanceDetailContent(
        uiState: InstanceDetailUiState,
        onBackClick: () -> Unit,
        onAddClick: () -> Unit,
    ) {
        val toolbarHeight = ToolbarTokens.ContainerHeight

        val contentCanScrollBackward = remember {
            mutableStateOf(false)
        }
        val loading = uiState.loading
        val instance = uiState.instance
        Scaffold { paddings ->
            CollapsableTopBarLayout(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(paddings),
                minTopBarHeight = toolbarHeight,
                contentCanScrollBackward = contentCanScrollBackward,
                topBar = { collapsableProgress ->
                    MotionLayout(
                        modifier = Modifier.fillMaxWidth(),
                        motionScene = buildMotionScene(toolbarHeight),
                        progress = collapsableProgress,
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .layoutId("banner")
                                .utopiaPlaceholder(loading),
                            model = uiState.instance?.thumbnail,
                            contentScale = ContentScale.Crop,
                            contentDescription = "Thumbnail",
                        )
                        Surface(modifier = Modifier.layoutId("content")) {
                            AppBarContent(uiState)
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
                                .utopiaPlaceholder(visible = loading),
                            model = instance?.thumbnail,
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
                        val actionProperties = motionProperties(id = "action")
                        Box(
                            modifier = Modifier
                                .height(ToolbarTokens.ContainerHeight)
                                .padding(end = ToolbarTokens.TopAppBarHorizontalPadding)
                                .layoutId("action"),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (uiState.addable) {
                                SimpleIconButton(
                                    onClick = onAddClick,
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Confirm add",
                                    tint = actionProperties.value.color("color"),
                                )
                            }
                        }
                        val toolbarTitleProperties = motionProperties(id = "toolbarTitle")
                        val toolbarTitleVisible = toolbarTitleProperties.value.float("visible")
                        Text(
                            modifier = Modifier.layoutId("toolbarTitle"),
                            text = instance?.title.orEmpty(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = if (toolbarTitleVisible >= 1F) 1F else 0F),
                        )
                    }
                }
            ) {
                if (instance != null) {
                    val coroutineScope = rememberCoroutineScope()
                    val tabs = remember {
                        InstanceDetailTab.entries.toTypedArray()
                    }
                    Column {
                        val pagerState = rememberPagerState(
                            initialPage = 0,
                            pageCount = tabs::size,
                        )
                        UtopiaTabRow(
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
                            if (uiState.baseUrl != null) {
                                tabs[currentPage].content(
                                    this@InstanceDetailScreen,
                                    uiState.baseUrl,
                                    instance.rules,
                                    contentCanScrollBackward,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMotionApi::class)
    @Composable
    private fun buildMotionScene(toolbarHeight: Dp) = MotionScene {
        val backIcon = createRefFor("backIcon")
        val actionRef = createRefFor("action")
        val toolbarPlaceholder = createRefFor("toolbarPlaceholder")

        val banner = createRefFor("banner")
        val contentRef = createRefFor("content")
        val avatar = createRefFor("avatar")
        val toolbarTitle = createRefFor("toolbarTitle")
        val bannerHeight = 120.dp
        val start1 = constraintSet {
            constrain(backIcon) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                customColor("color", Color(0xffffffff))
            }
            constrain(actionRef) {
                end.linkTo(parent.end)
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
            constrain(contentRef) {
                width = Dimension.fillToConstraints
                top.linkTo(parent.top, bannerHeight)
            }
            constrain(avatar) {
                start.linkTo(parent.start, 10.dp)
                top.linkTo(contentRef.top)
                bottom.linkTo(contentRef.top)
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
            constrain(actionRef) {
                end.linkTo(parent.end)
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
            constrain(contentRef) {
                width = Dimension.fillToConstraints
                bottom.linkTo(toolbarPlaceholder.bottom)
            }
            constrain(avatar) {
                start.linkTo(parent.start, 42.dp)
                top.linkTo(contentRef.top)
                bottom.linkTo(contentRef.top)
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

    @Composable
    private fun AppBarContent(
        uiState: InstanceDetailUiState,
    ) {
        val loading = uiState.loading
        val instance = uiState.instance
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
                    .utopiaPlaceholder(visible = loading),
                text = instance?.title.orEmpty(),
                style = MaterialTheme.typography.titleLarge,
            )
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .utopiaPlaceholder(visible = loading),
                text = uiState.baseUrl.toString(),
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .utopiaPlaceholder(visible = loading),
                text = instance?.description.orEmpty(),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
            )
            val languageString =
                instance?.languages?.joinToString(", ").orEmpty()
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .utopiaPlaceholder(visible = loading),
                text = stringResource(
                    R.string.activity_pub_instance_detail_language_label,
                    languageString
                ),
                maxLines = 3,
            )
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .utopiaPlaceholder(visible = loading),
                text = stringResource(
                    R.string.activity_pub_instance_detail_active_month_label,
                    instance?.usage?.users?.activeMonth.toString()
                ),
            )

            if (instance?.contact != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .utopiaPlaceholder(visible = loading),
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
                        fontWeight = FontWeight.Bold,
                    )

                    AsyncImage(
                        modifier = Modifier
                            .padding(start = 6.dp)
                            .size(18.dp)
                            .clip(CircleShape),
                        model = instance.contact.account.avatar,
                        contentDescription = "Mod avatar",
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 4.dp),
                        text = instance.contact.account.displayName,
                    )
                    Text(
                        modifier = Modifier
                            .padding(start = 4.dp),
                        text = instance.contact.email,
                    )
                }
            }
        }
    }
}
