package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.applyNestedScrollConnection
import com.zhangke.framework.composable.textString
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.common.composable.EmptyContent
import com.zhangke.fread.common.composable.EmptyContentType
import com.zhangke.fread.common.composable.ErrorContent
import com.zhangke.fread.common.composable.ErrorType
import com.zhangke.fread.commonbiz.shared.feeds.CommonFeedsUiState
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.account.isAuthenticationFailure
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.StatusListPlaceholder
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NewStatusNotifyBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.seconds

@Composable
fun FeedsContent(
    uiState: CommonFeedsUiState,
    openScreenFlow: SharedFlow<NavKey>,
    newStatusNotifyFlow: SharedFlow<Unit>?,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    composedStatusInteraction: ComposedStatusInteraction,
    nestedScrollConnection: NestedScrollConnection? = null,
    observeScrollToTopEvent: Boolean = false,
    onScrollToTopConsumed: (() -> Unit)? = null,
    contentCanScrollBackward: MutableState<Boolean>? = null,
    onImmersiveEvent: ((immersive: Boolean) -> Unit)? = null,
    onScrollInProgress: ((Boolean) -> Unit)? = null,
    onLoginClick: (() -> Unit)? = null,
) {
    ConsumeOpenScreenFlow(openScreenFlow)
    FeedsContent(
        feeds = uiState.feeds,
        refreshing = uiState.refreshing,
        loadMoreState = uiState.loadMoreState,
        showPagingLoadingPlaceholder = uiState.showPagingLoadingPlaceholder,
        pageErrorContent = uiState.pageErrorContent,
        newStatusNotifyFlow = newStatusNotifyFlow,
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
        composedStatusInteraction = composedStatusInteraction,
        nestedScrollConnection = nestedScrollConnection,
        observeScrollToTopEvent = observeScrollToTopEvent,
        onScrollToTopConsumed = onScrollToTopConsumed,
        contentCanScrollBackward = contentCanScrollBackward,
        onImmersiveEvent = onImmersiveEvent,
        onScrollInProgress = onScrollInProgress,
        onLoginClick = onLoginClick,
    )
}

@Composable
fun FeedsContent(
    feeds: List<StatusUiState>,
    refreshing: Boolean,
    loadMoreState: LoadState,
    showPagingLoadingPlaceholder: Boolean,
    pageErrorContent: Throwable?,
    newStatusNotifyFlow: SharedFlow<Unit>?,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    composedStatusInteraction: ComposedStatusInteraction,
    nestedScrollConnection: NestedScrollConnection? = null,
    observeScrollToTopEvent: Boolean = false,
    onScrollToTopConsumed: (() -> Unit)? = null,
    contentCanScrollBackward: MutableState<Boolean>? = null,
    onImmersiveEvent: ((immersive: Boolean) -> Unit)? = null,
    onScrollInProgress: ((Boolean) -> Unit)? = null,
    onLoginClick: (() -> Unit)? = null,
) {
    if (feeds.isEmpty()) {
        if (showPagingLoadingPlaceholder) {
            StatusListPlaceholder()
        } else if (pageErrorContent != null) {
            InitErrorContent(
                error = pageErrorContent,
                onLoginClick = onLoginClick,
                onRetryClick = onRefresh,
            )
        } else {
            EmptyListContent()
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            val state = rememberLoadableInlineVideoLazyColumnState(
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
            )
            val lazyListState = state.lazyListState
            if (contentCanScrollBackward != null) {
                val canScrollBackward by remember {
                    derivedStateOf {
                        lazyListState.firstVisibleItemIndex != 0 || lazyListState.firstVisibleItemScrollOffset != 0
                    }
                }
                contentCanScrollBackward.value = canScrollBackward
            }
            if (onImmersiveEvent != null) {
                ObserveForImmersive(
                    listState = lazyListState,
                    onImmersiveEvent = onImmersiveEvent,
                )
            }
            if (onScrollInProgress != null) {
                LaunchedEffect(lazyListState.isScrollInProgress) {
                    onScrollInProgress(lazyListState.isScrollInProgress)
                }
            }
            val feedsConnection = LocalNestedTabConnection.current
            if (observeScrollToTopEvent) {
                LaunchedEffect(feedsConnection, lazyListState) {
                    feedsConnection.scrollToTopFlow.collect {
                        if (lazyListState.layoutInfo.totalItemsCount > 0) {
                            lazyListState.scrollToItem(0)
                        }
                        onScrollToTopConsumed?.invoke()
                    }
                }
            }
            LoadableInlineVideoLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .applyNestedScrollConnection(nestedScrollConnection),
                state = state,
                refreshing = refreshing,
                loadState = loadMoreState,
            ) {
                itemsIndexed(feeds) { index, item ->
                    FeedsStatusNode(
                        modifier = Modifier.fillMaxWidth(),
                        status = item,
                        composedStatusInteraction = composedStatusInteraction,
                        indexInList = index,
                    )
                }
            }
            var showNewStatusNotifyBar by remember {
                mutableStateOf(false)
            }
            if (newStatusNotifyFlow != null) {
                ConsumeFlow(newStatusNotifyFlow) {
                    delay(1000)
                    if (state.lazyListState.firstVisibleItemIndex > 0) {
                        showNewStatusNotifyBar = true
                    }
                    delay(20.seconds)
                }
            }
            val coroutineScope = rememberCoroutineScope()
            AnimatedVisibility(
                modifier = Modifier
                    .padding(LocalContentPadding.current)
                    .padding(top = 32.dp)
                    .align(Alignment.TopCenter),
                visible = showNewStatusNotifyBar,
            ) {
                NewStatusNotifyBar(
                    modifier = Modifier,
                    onClick = {
                        showNewStatusNotifyBar = false
                        coroutineScope.launch {
                            if (feeds.isNotEmpty()) {
                                state.lazyListState.animateScrollToItem(0)
                                onScrollToTopConsumed?.invoke()
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun InitErrorContent(
    errorMessage: TextString,
    onRetryClick: (() -> Unit)? = null,
) {
    InitErrorContent(
        errorMessage = textString(text = errorMessage),
        onRetryClick = onRetryClick,
    )
}

@Composable
fun InitErrorContent(
    errorMessage: String,
    onRetryClick: (() -> Unit)? = null,
) {
    ErrorContent(
        modifier = Modifier.padding(LocalContentPadding.current).fillMaxSize(),
        type = ErrorType.Network,
        errorMessage = errorMessage,
        onRetryClick = onRetryClick ?: {},
    )
}

@Composable
fun InitErrorContent(
    error: Throwable,
    onLoginClick: (() -> Unit)? = null,
    onRetryClick: (() -> Unit)? = null,
) {
    if (onLoginClick != null && error.isAuthenticationFailure) {
        NotLoginPageError(
            modifier = Modifier.padding(LocalContentPadding.current),
            message = error.message,
            onLoginClick = onLoginClick,
        )
    } else {
        InitErrorContent(
            errorMessage = error.message.orEmpty(),
            onRetryClick = onRetryClick,
        )
    }
}

@Composable
fun NotLoginPageError(
    modifier: Modifier,
    message: String?,
    onLoginClick: () -> Unit,
) {
    EmptyContent(
        modifier = modifier.fillMaxSize(),
        type = EmptyContentType.Account,
        contentTitle = stringResource(LocalizedString.profileAccountNotLogin),
        subtitle = message,
        onClick = onLoginClick,
    )
}

@Composable
fun EmptyListContent() {
    EmptyContent(
        modifier = Modifier.fillMaxSize().padding(LocalContentPadding.current),
        type = EmptyContentType.Content,
        contentTitle = stringResource(LocalizedString.listContentEmptyPlaceholder),
        subtitle = null,
        onClick = null,
    )
}
