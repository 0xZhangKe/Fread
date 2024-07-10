package com.zhangke.fread.commonbiz.shared.screen.status.context

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.ui.BlogUi
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.style.defaultStatusStyle

@Composable
fun DescendantStatusUi(
    modifier: Modifier,
    status: StatusUiState,
    displayTime: String,
    style: StatusStyle = defaultStatusStyle(),
    indexInList: Int,
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    composedStatusInteraction: ComposedStatusInteraction,
    onMediaClick: OnBlogMediaClick,
) {
    val context = LocalContext.current
    val blog = status.status.intrinsicBlog
    Surface(
        modifier = modifier,
    ) {
        BlogUi(
            modifier = Modifier,
            blog = blog,
            displayTime = displayTime,
            indexInList = indexInList,
            style = style,
            bottomPanelInteractions = bottomPanelInteractions,
            moreInteractions = moreInteractions,
            onInteractive = {
                composedStatusInteraction.onStatusInteractive(status, it)
            },
            onMediaClick = onMediaClick,
            onUserInfoClick = {
                composedStatusInteraction.onUserInfoClick(status.role, it)
            },
            onVoted = {
                composedStatusInteraction.onVoted(status, it)
            },
            onHashtagInStatusClick = {
                composedStatusInteraction.onHashtagInStatusClick(status.role, it)
            },
            onMentionClick = {
                composedStatusInteraction.onMentionClick(status.role, it)
            },
            onUrlClick = {
                BrowserLauncher.launchWebTabInApp(context, it, status.role)
            },
            showDivider = true,
        )
    }
}
