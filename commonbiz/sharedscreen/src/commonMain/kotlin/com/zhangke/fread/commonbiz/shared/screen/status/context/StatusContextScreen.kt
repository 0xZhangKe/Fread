package com.zhangke.fread.commonbiz.shared.screen.status.context

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LoadErrorLineItem
import com.zhangke.framework.composable.LoadingLineItem
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.commonbiz.shared.composable.onStatusMediaClick
import com.zhangke.fread.commonbiz.shared.screen.status.account.SelectAccountOpenStatusBottomSheet
import com.zhangke.fread.commonbiz.shared.screen.status.account.rememberSelectAccountOpenStatusSheetState
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.StatusUi
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.threads.ThreadsType
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class StatusContextScreenNavKey(
    val locator: PlatformLocator,
    val serializedStatus: String? = null,
    val serializedBlog: String? = null,
    val blogId: String? = null,
    val platform: BlogPlatform? = null,
    val blogTranslationUiState: BlogTranslationUiState? = null,
) : NavKey {

    companion object {

        fun create(statusUiState: StatusUiState): NavKey {
            return StatusContextScreenNavKey(
                locator = statusUiState.locator,
                serializedStatus = globalJson.encodeToString(
                    kotlinx.serialization.serializer(),
                    statusUiState
                ),
                blogTranslationUiState = statusUiState.blogTranslationState,
            )
        }

        fun create(locator: PlatformLocator, blog: Blog): NavKey {
            return StatusContextScreenNavKey(
                locator = locator,
                serializedBlog = globalJson.encodeToString(
                    kotlinx.serialization.serializer(),
                    blog
                ),
                blogTranslationUiState = null,
            )
        }

        fun create(
            locator: PlatformLocator,
            blogId: String,
            platform: BlogPlatform
        ): NavKey {
            return StatusContextScreenNavKey(
                locator = locator,
                blogId = blogId,
                blogTranslationUiState = null,
                platform = platform,
            )
        }
    }
}

@Composable
fun StatusContextScreen(
    locator: PlatformLocator,
    serializedStatus: String? = null,
    serializedBlog: String? = null,
    blogId: String? = null,
    platform: BlogPlatform? = null,
    blogTranslationUiState: BlogTranslationUiState? = null,
    containerViewModel: StatusContextViewModel,
) {
    val backStack = LocalNavBackStack.currentOrThrow
    val viewModel = containerViewModel.getSubViewModel(
        locator = locator,
        anchorStatus = serializedStatus?.let(globalJson::decodeFromString),
        blog = serializedBlog?.let { globalJson.decodeFromString(it) },
        blogId = blogId,
        platform = platform,
        blogTranslationUiState = blogTranslationUiState,
    )
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = rememberSnackbarHostState()
    StatusContextContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onScrolledToAnchor = viewModel::onScrolledToAnchor,
        onMediaClick = { event ->
            onStatusMediaClick(
                navigator = backStack,
                event = event,
            )
        },
        onBackClick = backStack::removeLastOrNull,
        onAccountClick = viewModel::onAccountClick,
        composedStatusInteraction = viewModel.composedStatusInteraction,
    )
    LaunchedEffect(Unit) {
        viewModel.onPageResume()
    }
    ConsumeOpenScreenFlow(viewModel.openScreenFlow)
    ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
}

@Composable
private fun StatusContextContent(
    uiState: StatusContextUiState,
    snackbarHostState: SnackbarHostState,
    onScrolledToAnchor: () -> Unit,
    onBackClick: () -> Unit = {},
    onAccountClick: (LoggedAccount) -> Unit,
    onMediaClick: OnBlogMediaClick,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.sharedStatusContextScreenTitle),
                onBackClick = onBackClick,
                actions = {
                    if (uiState.currentAccount != null) {
                        BlogAuthorAvatar(
                            modifier = Modifier.padding(end = 8.dp)
                                .size(28.dp),
                            imageUrl = uiState.currentAccount.avatar,
                            onClick = { onAccountClick(uiState.currentAccount) },
                        )
                    }
                }
            )
        },
        content = { contentPaddings ->
            val contextStatus = uiState.contextStatus
            if (contextStatus.isEmpty()) return@Scaffold
            val state = rememberLazyListState()
            val anchorIndex = uiState.anchorIndex
            if (!uiState.loading && uiState.needScrollToAnchor && anchorIndex in 0..contextStatus.lastIndex) {
                LaunchedEffect(anchorIndex) {
                    state.animateScrollToItem(anchorIndex)
                    onScrolledToAnchor()
                }
            }
            InlineVideoLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPaddings),
                state = state,
            ) {
                itemsIndexed(
                    items = contextStatus,
                ) { index, statusInContext ->
                    StatusInContextUi(
                        modifier = Modifier
                            .fillMaxWidth(),
                        statusInContext = statusInContext,
                        indexInList = index,
                        onMediaClick = onMediaClick,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }
                if (uiState.loading) {
                    item {
                        LoadingLineItem(modifier = Modifier.fillMaxWidth())
                    }
                } else if (uiState.errorMessage != null) {
                    item {
                        LoadErrorLineItem(
                            modifier = Modifier.fillMaxWidth(),
                            errorMessage = uiState.errorMessage,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun StatusInContextUi(
    modifier: Modifier = Modifier,
    statusInContext: StatusInContext,
    indexInList: Int,
    onMediaClick: OnBlogMediaClick,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val selectAccountOpenStatusBottomSheetState = rememberSelectAccountOpenStatusSheetState()
    when (statusInContext.type) {
        StatusInContextType.ANCESTOR -> StatusUi(
            modifier = modifier.clickable {
                composedStatusInteraction.onStatusClick(statusInContext.status)
            },
            threadsType = if (indexInList == 0) {
                ThreadsType.FIRST_ANCESTOR
            } else {
                ThreadsType.ANCESTOR
            },
            status = statusInContext.status,
            indexInList = indexInList,
            onMediaClick = onMediaClick,
            composedStatusInteraction = composedStatusInteraction,
            onOpenBlogWithOtherAccountClick = {
                selectAccountOpenStatusBottomSheetState.show(it)
            },
        )

        StatusInContextType.ANCHOR -> StatusUi(
            modifier = modifier,
            status = statusInContext.status,
            indexInList = indexInList,
            threadsType = if (indexInList == 0) ThreadsType.ANCHOR_FIRST else ThreadsType.ANCHOR,
            onMediaClick = onMediaClick,
            detailModel = true,
            composedStatusInteraction = composedStatusInteraction,
            onOpenBlogWithOtherAccountClick = {
                selectAccountOpenStatusBottomSheetState.show(it)
            },
        )

        StatusInContextType.DESCENDANT -> StatusUi(
            modifier = modifier.clickable {
                composedStatusInteraction.onStatusClick(statusInContext.status)
            },
            status = statusInContext.status,
            indexInList = indexInList,
            onMediaClick = onMediaClick,
            threadsType = ThreadsType.NONE,
            composedStatusInteraction = composedStatusInteraction,
            onOpenBlogWithOtherAccountClick = {
                selectAccountOpenStatusBottomSheetState.show(it)
            },
        )

        StatusInContextType.DESCENDANT_ANCHOR -> StatusUi(
            modifier = modifier.clickable {
                composedStatusInteraction.onStatusClick(statusInContext.status)
            },
            threadsType = ThreadsType.FIRST_ANCESTOR,
            status = statusInContext.status,
            indexInList = indexInList,
            onMediaClick = onMediaClick,
            composedStatusInteraction = composedStatusInteraction,
            onOpenBlogWithOtherAccountClick = {
                selectAccountOpenStatusBottomSheetState.show(it)
            },
        )

        StatusInContextType.DESCENDANT_WITH_ANCESTOR_DESCENDANT -> StatusUi(
            modifier = modifier.clickable {
                composedStatusInteraction.onStatusClick(statusInContext.status)
            },
            threadsType = ThreadsType.ANCESTOR,
            status = statusInContext.status,
            indexInList = indexInList,
            onMediaClick = onMediaClick,
            composedStatusInteraction = composedStatusInteraction,
            onOpenBlogWithOtherAccountClick = {
                selectAccountOpenStatusBottomSheetState.show(it)
            },
        )

        StatusInContextType.DESCENDANT_WITH_ANCESTOR -> StatusUi(
            modifier = modifier.clickable {
                composedStatusInteraction.onStatusClick(statusInContext.status)
            },
            threadsType = ThreadsType.ANCHOR,
            status = statusInContext.status,
            indexInList = indexInList,
            onMediaClick = onMediaClick,
            composedStatusInteraction = composedStatusInteraction,
            onOpenBlogWithOtherAccountClick = {
                selectAccountOpenStatusBottomSheetState.show(it)
            },
        )
    }
    SelectAccountOpenStatusBottomSheet(selectAccountOpenStatusBottomSheetState)
}
