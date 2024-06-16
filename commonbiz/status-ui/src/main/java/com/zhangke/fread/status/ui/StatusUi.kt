package com.zhangke.fread.status.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Surface(
        modifier = modifier,
    ) {
        when (val rawStatus = status.status) {
            is Status.Reblog -> {
                ReblogUi(
                    modifier = Modifier,
                    reblog = rawStatus,
                    displayTime = status.displayTime,
                    indexInList = indexInList,
                    bottomPanelInteractions = status.bottomInteractions,
                    moreInteractions = status.moreInteractions,
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
                )
            }

            is Status.NewBlog -> {
                BlogUi(
                    modifier = Modifier,
                    blog = rawStatus.blog,
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
                )
            }
        }
    }
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
