package com.zhangke.fread.status.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.label.ReblogTopLabel
import com.zhangke.fread.status.ui.label.StatusPinnedLabel
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.style.defaultStatusStyle

@Composable
fun StatusUi(
    modifier: Modifier = Modifier,
    status: StatusUiState,
    indexInList: Int,
    style: StatusStyle = defaultStatusStyle(),
    onMediaClick: OnBlogMediaClick,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val context = LocalContext.current
    Surface(modifier = modifier) {
        val rawStatus = status.status
        BlogUi(
            modifier = Modifier,
            blog = rawStatus.intrinsicBlog,
            topLabel = getStatusTopLabel(status, style, composedStatusInteraction),
            displayTime = status.displayTime,
            bottomPanelInteractions = status.bottomInteractions,
            moreInteractions = status.moreInteractions,
            indexInList = indexInList,
            style = style,
            onInteractive = {
                composedStatusInteraction.onStatusInteractive(status, it)
            },
            onMediaClick = onMediaClick,
            onUserInfoClick = {
                composedStatusInteraction.onUserInfoClick(status.role, it)
            },
            onVoted = { options ->
                composedStatusInteraction.onVoted(status, options)
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
        )
    }
}

private fun getStatusTopLabel(
    statusUiState: StatusUiState,
    style: StatusStyle,
    composedStatusInteraction: ComposedStatusInteraction,
): (@Composable () -> Unit)? {
    val rawStatus = statusUiState.status
    if (rawStatus is Status.Reblog) {
        return {
            ReblogTopLabel(
                author = rawStatus.author,
                style = style,
                onAuthorClick = {
                    composedStatusInteraction.onUserInfoClick(statusUiState.role, it)
                },
            )
        }
    }
    if (rawStatus.intrinsicBlog.pinned) {
        return {
            StatusPinnedLabel(
                style = style,
            )
        }
    }
    return null
}

interface ComposedStatusInteraction {

    fun onStatusInteractive(status: StatusUiState, interaction: StatusUiInteraction)
    fun onUserInfoClick(role: IdentityRole, blogAuthor: BlogAuthor)
    fun onVoted(status: StatusUiState, blogPollOptions: List<BlogPoll.Option>)
    fun onHashtagInStatusClick(role: IdentityRole, hashtagInStatus: HashtagInStatus)
    fun onHashtagClick(role: IdentityRole, tag: Hashtag)
    fun onMentionClick(role: IdentityRole, mention: Mention)
    fun onStatusClick(status: StatusUiState)
    fun onFollowClick(role: IdentityRole, target: BlogAuthor)
    fun onUnfollowClick(role: IdentityRole, target: BlogAuthor)
    // 后面如果需要给 Status 加上 follow 按钮，这里可以新增一个 onFollowClick(Status, BlogAuthor)
}
