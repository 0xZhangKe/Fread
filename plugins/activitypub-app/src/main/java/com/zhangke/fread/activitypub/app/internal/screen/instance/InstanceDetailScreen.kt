package com.zhangke.fread.activitypub.app.internal.screen.instance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.FreadTabRow
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.textString
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.composable.ScrollUpTopBarLayout
import com.zhangke.fread.activitypub.app.internal.screen.user.DetailHeaderContent
import com.zhangke.fread.activitypub.app.internal.screen.user.DetailTopBar
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import kotlinx.coroutines.launch

@Destination(PlatformDetailRoute.ROUTE)
class InstanceDetailScreen(
    @Router private val route: String = "",
    private val baseUrl: FormalBaseUrl? = null,
) : BaseScreen() {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val navigationResult = navigator.navigationResult
        val viewModel = getViewModel<InstanceDetailViewModel, InstanceDetailViewModel.Factory> {
            if (baseUrl != null) {
                it.create(baseUrl)
            } else {
                val baseUrl = PlatformDetailRoute.parseParams(route)
                it.create(baseUrl)
            }
        }
        val uiState by viewModel.uiState.collectAsState()
        InstanceDetailContent(
            uiState = uiState,
            onBackClick = {
                if (!navigator.pop()) {
                    navigationResult.popWithResult(false)
                }
            },
        )
    }

    @Composable
    private fun InstanceDetailContent(
        uiState: InstanceDetailUiState,
        onBackClick: () -> Unit,
    ) {
        val contentCanScrollBackward = remember {
            mutableStateOf(false)
        }
        val instance = uiState.instance
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { innerPaddings ->
            ScrollUpTopBarLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPaddings),
                topBarContent = { progress ->
                    DetailTopBar(
                        progress = progress,
                        title = buildRichText(instance?.title.orEmpty()),
                        onBackClick = onBackClick,
                        actions = {},
                    )
                },
                headerContent = { progress ->
                    AppBarContent(
                        progress = progress,
                        uiState = uiState,
                    )
                },
                contentCanScrollBackward = contentCanScrollBackward,
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
                        FreadTabRow(
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

    @Composable
    private fun AppBarContent(
        progress: Float,
        uiState: InstanceDetailUiState,
    ) {
        val loading = uiState.loading
        val instance = uiState.instance
        DetailHeaderContent(
            progress = progress,
            loading = loading,
            banner = instance?.thumbnail?.url,
            avatar = instance?.thumbnail?.url,
            title = buildRichText(instance?.title.orEmpty()),
            description = buildRichText(instance?.description.orEmpty()),
            acctLine = {
                Text(
                    text = uiState.baseUrl.toString(),
                    style = MaterialTheme.typography.labelMedium,
                )
            },
            followInfo = {
                Column {

                    val languageString =
                        instance?.languages?.joinToString(", ").orEmpty()
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .freadPlaceholder(visible = loading),
                        text = stringResource(
                            R.string.activity_pub_instance_detail_language_label,
                            languageString
                        ),
                        maxLines = 3,
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .freadPlaceholder(visible = loading),
                        text = stringResource(
                            R.string.activity_pub_instance_detail_active_month_label,
                            instance?.usage?.users?.activeMonth.toString()
                        ),
                    )

                    if (instance?.contact != null) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .freadPlaceholder(visible = loading),
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
            },
            onBannerClick = {

            },
            onAvatarClick = {

            },
        )
    }
}
