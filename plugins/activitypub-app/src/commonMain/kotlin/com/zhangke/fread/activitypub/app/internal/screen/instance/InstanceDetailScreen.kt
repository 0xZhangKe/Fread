package com.zhangke.fread.activitypub.app.internal.screen.instance

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.FreadTabRow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.textString
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreenKey
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.handler.LocalTextHandler
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.richtext.buildRichText
import com.zhangke.fread.status.ui.action.DropDownCopyLinkItem
import com.zhangke.fread.status.ui.action.DropDownOpenInBrowserItem
import com.zhangke.fread.status.ui.common.DetailPageScaffold
import com.zhangke.fread.status.ui.richtext.FreadRichText
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class InstanceDetailScreenKey(
    val locator: PlatformLocator,
    val baseUrl: FormalBaseUrl,
) : NavKey

@Composable
fun InstanceDetailScreen(locator: PlatformLocator, viewModel: InstanceDetailViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val backStack = LocalNavBackStack.currentOrThrow
    InstanceDetailContent(
        uiState = uiState,
        onBackClick = {
            if (!navigator.pop()) {
                navigationResult.popWithResult(false)
            }
        },
        onUserClick = { _, webFinger, userId ->
            backStack.add(
                UserDetailScreenKey(
                    locator = locator,
                    webFinger = webFinger,
                    userId = userId,
                )
            )
        }
    )
}

@Composable
private fun InstanceDetailContent(
    uiState: InstanceDetailUiState,
    onBackClick: () -> Unit,
    onUserClick: (PlatformLocator, WebFinger, String?) -> Unit,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    val contentCanScrollBackward = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    DetailPageScaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHostState = rememberSnackbarHostState(),
        title = buildRichText(uiState.instance?.title.orEmpty()),
        loading = uiState.loading,
        avatar = uiState.instance?.thumbnail?.url.orEmpty(),
        banner = uiState.instance?.thumbnail?.url,
        privateNote = null,
        topBarActions = {
            val baseUrl = uiState.baseUrl
            if (baseUrl != null) {
                InstanceDetailActions(baseUrl)
            }
        },
        contentCanScrollBackward = contentCanScrollBackward,
        description = buildRichText(uiState.instance?.description.orEmpty()),
        handleLine = {
            Text(
                text = uiState.baseUrl.toString(),
                style = MaterialTheme.typography.labelMedium,
            )
        },
        followInfoLine = {
            InstanceModLine(
                uiState = uiState,
                onUserClick = onUserClick,
            )
        },
        onBackClick = onBackClick,
        onUrlClick = {
            coroutineScope.launch {
                browserLauncher.launchWebTabInApp(it, locator)
            }
        },
        onMaybeHashtagClick = {},
        onBannerClick = {},
        onAvatarClick = {},
        topDetailContentAction = null,
        bottomArea = null,
    ) {
        if (uiState.instance != null) {
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
                            uiState.instance.rules,
                            contentCanScrollBackward,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InstanceModLine(
    uiState: InstanceDetailUiState,
    onUserClick: (PlatformLocator, WebFinger, String?) -> Unit,
) {
    val instance = uiState.instance
    val loading = uiState.loading
    Column {
        val languageString = instance?.languages?.joinToString(", ").orEmpty()
        Text(
            modifier = Modifier
                .padding(top = 4.dp)
                .freadPlaceholder(visible = loading),
            text = stringResource(
                LocalizedString.activity_pub_instance_detail_language_label,
                languageString
            ),
            maxLines = 3,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            modifier = Modifier
                .padding(top = 4.dp)
                .freadPlaceholder(visible = loading),
            text = stringResource(
                LocalizedString.activity_pub_instance_detail_active_month_label,
                instance?.usage?.users?.activeMonth.toString()
            ),
            style = MaterialTheme.typography.bodyMedium,
        )

        if (instance?.contact != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .freadPlaceholder(visible = loading)
                    .noRippleClick {
                        val account = uiState.modAccount ?: return@noRippleClick
                        val role =
                            PlatformLocator(accountUri = account.uri, baseUrl = baseUrl)
                        onUserClick(role, account.webFinger, account.userId)
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 6.dp),
                    text = "MOD",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                )

                AutoSizeImage(
                    instance.contact?.account?.avatar.orEmpty(),
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(22.dp)
                        .clip(CircleShape),
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
}

@Composable
private fun InstanceDetailActions(baseUrl: FormalBaseUrl) {
    val activityTextHandler = LocalTextHandler.current
    val browserLauncher = LocalActivityBrowserLauncher.current
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
            showMorePopup = false
            browserLauncher.launchBySystemBrowser(baseUrl.toString())
        }

        DropDownCopyLinkItem {
            showMorePopup = false
            activityTextHandler.copyText(baseUrl.toString())
        }
    }
}
