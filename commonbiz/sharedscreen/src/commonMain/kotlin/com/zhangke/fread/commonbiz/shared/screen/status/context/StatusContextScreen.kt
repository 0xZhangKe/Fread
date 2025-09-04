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
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LoadErrorLineItem
import com.zhangke.framework.composable.LoadingLineItem
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.security.Md5
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.shared.composable.onOpenBlogWithOtherAccountClick
import com.zhangke.fread.commonbiz.shared.composable.onStatusMediaClick
import com.zhangke.fread.commonbiz.shared.screen.shared_status_context_screen_title
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.StatusUi
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.threads.ThreadsType
import kotlinx.serialization.serializer
import org.jetbrains.compose.resources.stringResource

data class StatusContextScreen(
    val locator: PlatformLocator,
    val serializedStatus: String? = null,
    val serializedBlog: String? = null,
    val blogTranslationUiState: BlogTranslationUiState? = null,
) : BaseScreen() {

    companion object {

        fun create(
            statusUiState: StatusUiState,
        ): StatusContextScreen {
            return StatusContextScreen(
                locator = statusUiState.locator,
                serializedStatus = globalJson.encodeToString(serializer(), statusUiState),
                blogTranslationUiState = statusUiState.blogTranslationState,
            )
        }

        fun create(locator: PlatformLocator, blog: Blog): StatusContextScreen {
            return StatusContextScreen(
                locator = locator,
                serializedBlog = globalJson.encodeToString(serializer(), blog),
                blogTranslationUiState = null,
            )
        }
    }

    override val key: ScreenKey =
        locator.toString() + serializedStatus?.let(Md5::md5) + serializedBlog?.let(Md5::md5)

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val transparentNavigator = LocalTransparentNavigator.current
        val viewModel = getViewModel<StatusContextViewModel>().getSubViewModel(
            locator = locator,
            anchorStatus = serializedStatus?.let(globalJson::decodeFromString),
            blog = serializedBlog?.let { globalJson.decodeFromString(it) },
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
                    transparentNavigator = transparentNavigator,
                    navigator = navigator,
                    event = event,
                )
            },
            onBackClick = navigator::pop,
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
                    title = stringResource(com.zhangke.fread.commonbiz.shared.screen.Res.string.shared_status_context_screen_title),
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
        val bottomNavigator = LocalBottomSheetNavigator.current
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
                    onOpenBlogWithOtherAccountClick(bottomNavigator, it)
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
                    onOpenBlogWithOtherAccountClick(bottomNavigator, it)
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
                    onOpenBlogWithOtherAccountClick(bottomNavigator, it)
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
                    onOpenBlogWithOtherAccountClick(bottomNavigator, it)
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
                    onOpenBlogWithOtherAccountClick(bottomNavigator, it)
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
                    onOpenBlogWithOtherAccountClick(bottomNavigator, it)
                },
            )
        }
    }
}
