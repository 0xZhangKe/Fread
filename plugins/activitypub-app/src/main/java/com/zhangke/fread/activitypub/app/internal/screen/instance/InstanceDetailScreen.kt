package com.zhangke.fread.activitypub.app.internal.screen.instance

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.FreadTabRow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.textString
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.SystemUtils
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.ActivityPubDataElements
import com.zhangke.fread.activitypub.app.internal.composable.ScrollUpTopBarLayout
import com.zhangke.fread.activitypub.app.internal.screen.user.DetailHeaderContent
import com.zhangke.fread.activitypub.app.internal.screen.user.DetailTopBar
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.fread.status.ui.action.DropDownCopyLinkItem
import com.zhangke.fread.status.ui.action.DropDownOpenInBrowserItem
import com.zhangke.fread.status.ui.richtext.FreadRichText
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
            onUserClick = { role, webFinger ->
                navigator.push(UserDetailScreen(role = role, webFinger = webFinger))
            }
        )
    }

    @Composable
    private fun InstanceDetailContent(
        uiState: InstanceDetailUiState,
        onBackClick: () -> Unit,
        onUserClick: (IdentityRole, WebFinger) -> Unit,
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
                        actions = {
                            val baseUrl = uiState.baseUrl
                            if (baseUrl != null) {
                                InstanceDetailActions(baseUrl)
                            }
                        },
                    )
                },
                headerContent = { progress ->
                    AppBarContent(
                        progress = progress,
                        uiState = uiState,
                        onUserClick = onUserClick,
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
                            tabCount = tabs.size,
                            selectedTabIndex = pagerState.currentPage,
                            tabContent = { index ->
                                Text(text = textString(tabs[index].title))
                            },
                            onTabClick = { index ->
                                coroutineScope.launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                        )
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
        onUserClick: (IdentityRole, WebFinger) -> Unit,
    ) {
        val context = LocalContext.current
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
                                .freadPlaceholder(visible = loading)
                                .noRippleClick {
                                    val account = uiState.modAccount ?: return@noRippleClick
                                    val role = IdentityRole(accountUri = account.uri, baseUrl)
                                    onUserClick(role, account.webFinger)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primaryContainer,
                                        RoundedCornerShape(4.dp),
                                    )
                                    .padding(horizontal = 4.dp),
                                text = "MOD",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )

                            AsyncImage(
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .size(22.dp)
                                    .clip(CircleShape),
                                model = instance.contact?.account?.avatar,
                                contentDescription = "Mod avatar",
                            )
                            FreadRichText(
                                modifier = Modifier
                                    .padding(start = 4.dp),
                                content = instance.contact?.account?.displayName.orEmpty(),
                                emojis = uiState.modAccount?.emojis ?: emptyList(),
                                onUrlClick = {},
                            )
                        }
                    }
                }
            },
            onBannerClick = {

            },
            onAvatarClick = {

            },
            onUrlClick = {
                val role = if (uiState.baseUrl != null) {
                    IdentityRole(null, baseUrl = uiState.baseUrl)
                } else {
                    null
                }
                BrowserLauncher.launchWebTabInApp(context, it, role)
            },
            onMaybeHashtagTargetClick = {},
        )
    }

    @Composable
    private fun InstanceDetailActions(baseUrl: FormalBaseUrl) {
        val context = LocalContext.current
        var showMorePopup by remember {
            mutableStateOf(false)
        }
        SimpleIconButton(
            onClick = { showMorePopup = true },
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More Options"
        )
        DropdownMenu(
            expanded = showMorePopup,
            onDismissRequest = { showMorePopup = false },
        ) {
            DropDownOpenInBrowserItem {
                reportClick(ActivityPubDataElements.INSTANCE_DETAIL_OPEN_IN_BROWSER)
                showMorePopup = false
                BrowserLauncher.launchBySystemBrowser(context, baseUrl.toString())
            }

            DropDownCopyLinkItem {
                reportClick(ActivityPubDataElements.INSTANCE_DETAIL_COPY_LINK)
                showMorePopup = false
                SystemUtils.copyText(context, baseUrl.toString())
            }
        }
    }
}
