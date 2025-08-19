package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.ui.BlogUi
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.getStatusTopLabel
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.threads.ThreadsType

@Composable
fun BlogUi(
    modifier: Modifier,
    blog: Blog,
    locator: PlatformLocator,
    indexInList: Int,
    style: StatusStyle,
    showBottomPanel: Boolean,
    showMoreOperationIcon: Boolean,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val browserLauncher = LocalActivityBrowserLauncher.current
    val navigator = LocalNavigator.currentOrThrow
    val transparentNavigator = LocalTransparentNavigator.current
    val fixedThreadType = if (blog.isReply) {
        ThreadsType.CONTINUED_THREAD
    } else {
        ThreadsType.NONE
    }
    var continueThreadHeight: Int? by remember { mutableStateOf(null) }
    Box(modifier = modifier) {
        BlogUi(
            modifier = Modifier,
            blog = blog,
            logged = null,
            isOwner = null,
            blogTranslationState = BlogTranslationUiState.DEFAULT,
            indexInList = indexInList,
            showMoreOperationIcon = showMoreOperationIcon,
            style = style,
            threadsType = fixedThreadType,
            continueThreadLabelHeight = continueThreadHeight,
            topLabels = getStatusTopLabel(
                isReblog = false,
                pinned = blog.pinned,
                isReply = blog.isReply,
                author = blog.author,
                mentionOnly = blog.visibility == StatusVisibility.DIRECT,
                style = style,
                threadsType = fixedThreadType,
                onUserInfoClick = {
                    composedStatusInteraction.onUserInfoClick(locator, it)
                },
                onContinueThreadHeightChanged = { continueThreadHeight = it }
            ),
            onInteractive = { type, _ -> },
            onUserInfoClick = {
                composedStatusInteraction.onUserInfoClick(locator, it)
            },
            onMediaClick = { event ->
                onStatusMediaClick(
                    transparentNavigator = transparentNavigator,
                    navigator = navigator,
                    event = event,
                )
            },
            showDivider = false,
            showBottomPanel = showBottomPanel,
            onVoted = {},
            onHashtagInStatusClick = {
                composedStatusInteraction.onHashtagInStatusClick(locator, it)
            },
            onMentionClick = {
                composedStatusInteraction.onMentionClick(locator, it)
            },
            onMentionDidClick = {
                composedStatusInteraction.onMentionClick(
                    locator = locator,
                    did = it,
                    protocol = blog.platform.protocol,
                )
            },
            onUrlClick = {
                browserLauncher.launchWebTabInApp(it, locator)
            },
            onShowOriginalClick = {},
            onTranslateClick = {},
            onBlogClick = {
                composedStatusInteraction.onBlockClick(locator, it)
            },
            onMaybeHashtagClick = {
                composedStatusInteraction.onMaybeHashtagClick(
                    locator = locator,
                    protocol = blog.platform.protocol,
                    hashtag = it,
                )
            },
        )
    }
}
