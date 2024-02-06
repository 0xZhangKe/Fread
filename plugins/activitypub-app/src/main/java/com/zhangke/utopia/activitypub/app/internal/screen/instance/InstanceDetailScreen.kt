package com.zhangke.utopia.activitypub.app.internal.screen.instance

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.UtopiaTabRow
import com.zhangke.framework.composable.textString
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.composable.CollapsableTopBarScaffold
import com.zhangke.utopia.commonbiz.shared.screen.login.LoginBottomSheetScreen
import kotlinx.coroutines.launch

@Destination(PlatformDetailRoute.ROUTE)
class InstanceDetailScreen(
    @Router private val route: String = "",
) : Screen {

    @Composable
    override fun Content() {
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

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun InstanceDetailContent(
        uiState: InstanceDetailUiState,
        onBackClick: () -> Unit,
        onAddClick: () -> Unit,
    ) {
        val contentCanScrollBackward = remember {
            mutableStateOf(false)
        }
        val instance = uiState.instance
        Scaffold { paddings ->
            CollapsableTopBarScaffold(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(paddings),
                title = instance?.title,
                banner = uiState.instance?.thumbnail?.url,
                avatar = instance?.thumbnail?.url,
                contentCanScrollBackward = contentCanScrollBackward,
                onBackClick = onBackClick,
                toolbarAction = { fontColor ->
                    if (uiState.addable) {
                        SimpleIconButton(
                            onClick = onAddClick,
                            imageVector = Icons.Default.Check,
                            contentDescription = "Confirm add",
                            tint = fontColor,
                        )
                    }
                },
                headerAction = { },
                headerContent = {
                    AppBarContent(uiState)
                },
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

    @Composable
    private fun AppBarContent(
        uiState: InstanceDetailUiState,
    ) {
        val loading = uiState.loading
        val instance = uiState.instance
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
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
