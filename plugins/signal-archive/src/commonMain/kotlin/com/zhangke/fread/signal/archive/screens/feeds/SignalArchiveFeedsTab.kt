package com.zhangke.fread.signal.archive.screens.feeds

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.loadable.lazycolumn.PullToRefreshIndicator
import com.zhangke.framework.nav.TabOptions
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.signal.archive.screens.detail.SignalArchiveDetailNavKey
import com.zhangke.fread.signal.archive.model.SignalArchiveArticleSummary
import com.zhangke.fread.signal.archive.model.SignalArchiveIndex
import com.zhangke.fread.signal.archive.model.SignalArchiveNode
import com.zhangke.fread.signal.archive.screens.ArchiveMessage
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.ObserveScrollInProgressForConnection
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** A node and its owning archive; index position is scoped to the current snapshot. */
@Serializable
data class SignalArchiveFeedsTabInfo(
    val contentId: String,
    val baseUrl: String,
    val nodeIndex: Int,
    val node: SignalArchiveNode,
    val generation: Int,
) : PlatformSerializable

class SignalArchiveFeedsTab(
    private val info: SignalArchiveFeedsTabInfo,
) : BaseTab() {
    override val options: TabOptions
        @Composable get() = TabOptions(title = info.node.title)

    @Composable
    override fun Content() {
        Content(active = true, onIndexUpdated = null)
    }

    @Composable
    fun Content(
        active: Boolean,
        onIndexUpdated: ((SignalArchiveIndex) -> Unit)?,
        containerRefreshing: Boolean = false,
    ) {
        val viewModel = koinViewModel<SignalArchiveFeedsViewModel>(
            key = "${info.contentId}:feeds:${info.nodeIndex}",
        ) { parametersOf(info) }
        val state by viewModel.uiState.collectAsState()
        val backStack = LocalNavBackStack.currentOrThrow
        LaunchedEffect(info, active) {
            viewModel.updateTab(info)
            if (active) viewModel.ensureLoaded()
        }
        LaunchedEffect(viewModel, onIndexUpdated) {
            viewModel.indexUpdates.collect { index ->
                if (onIndexUpdated != null) onIndexUpdated(index) else viewModel.acceptIndex(index)
            }
        }
        key(info.generation) {
            SignalArchiveFeedsContent(
                feeds = state,
                hasMore = viewModel.hasMorePages(),
                refreshing = containerRefreshing || state.refreshing || (state.loading && state.items.isEmpty()),
                active = active,
                contentPadding = com.zhangke.framework.composable.LocalContentPadding.current,
                onRefresh = viewModel::refresh,
                onLoadMore = viewModel::loadMore,
                onArticleClick = { article ->
                    backStack.add(SignalArchiveDetailNavKey(info.baseUrl, article.detailPath, article.title))
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignalArchiveFeedsContent(
    feeds: SignalArchiveFeedsUiState,
    hasMore: Boolean,
    refreshing: Boolean,
    active: Boolean,
    contentPadding: PaddingValues,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onArticleClick: (SignalArchiveArticleSummary) -> Unit,
) {
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullToRefreshState()
    val connection = LocalNestedTabConnection.current
    val layoutDirection = LocalLayoutDirection.current
    if (active) {
        ObserveScrollInProgressForConnection(listState)
        LaunchedEffect(connection, listState) {
            connection.scrollToTopFlow.collect { listState.animateScrollToItem(0) }
        }
    }
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        state = pullRefreshState,
        isRefreshing = refreshing,
        onRefresh = onRefresh,
        indicator = {
            PullToRefreshIndicator(
                state = pullRefreshState,
                refreshing = refreshing,
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            contentPadding = PaddingValues(
                start = contentPadding.calculateStartPadding(layoutDirection),
                end = contentPadding.calculateEndPadding(layoutDirection),
                top = contentPadding.calculateTopPadding() + 8.dp,
                bottom = contentPadding.calculateBottomPadding() + 16.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(feeds.items, key = { "article:${it.id}" }) { article ->
                SignalArchiveArticleCard(article) {
                    onArticleClick(article)
                }
            }
            if (feeds.items.isNotEmpty()) {
                item(key = "pagination") {
                    when {
                        feeds.loading && !refreshing -> ArchiveMessage(loading = true)
                        feeds.failed -> ArchiveMessage(
                            failed = true,
                            pageFailed = !feeds.indexFailed,
                            onRetry = if (feeds.indexFailed) onRefresh else onLoadMore,
                        )

                        hasMore -> {
                            if (active && !refreshing) {
                                LaunchedEffect(feeds.nextPage) { onLoadMore() }
                            }
                        }

                    }
                }
            }
        }
        if (feeds.items.isEmpty()) {
            // Empty/loading placeholders must not become lazy-list scroll anchors.
            if (!refreshing) {
                ArchiveMessage(
                    loading = feeds.loading || (hasMore && !feeds.failed),
                    failed = feeds.failed,
                    pageFailed = !feeds.indexFailed,
                    onRetry = if (feeds.indexFailed) onRefresh else onLoadMore,
                )
            }
            if (active && !refreshing && !feeds.loading && !feeds.failed && hasMore) {
                LaunchedEffect(feeds.nextPage) { onLoadMore() }
            }
        }
    }
}

@Composable
private fun SignalArchiveArticleCard(article: SignalArchiveArticleSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        onClick = onClick,
    ) {
        Column {
            article.image?.takeIf { it.isNotBlank() }?.let { image ->
                AutoSizeImage(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    url = image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(article.title, style = MaterialTheme.typography.titleMedium)
                if (article.summary.isNotBlank()) {
                    Text(
                        article.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    listOfNotNull(article.sourceTitle, article.publishedAt).joinToString(" · "),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
