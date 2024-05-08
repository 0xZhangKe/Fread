package com.zhangke.utopia.status.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

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
                        composedStatusInteraction.onStatusInteractive(status.status, it)
                    },
                    onMediaClick = onMediaClick,
                    onUserInfoClick = composedStatusInteraction::onUserInfoClick,
                    onVoted = { options ->
                        composedStatusInteraction.onVoted(status.status, options)
                    },
                    onHashtagInStatusClick = { blogAuthor, hashtagInStatus ->
                        composedStatusInteraction.onHashtagInStatusClick(blogAuthor, hashtagInStatus)
                    },
                    onMentionClick = { blogAuthor, mention ->
                        composedStatusInteraction.onMentionClick(blogAuthor, mention)
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
                        composedStatusInteraction.onStatusInteractive(status.status, it)
                    },
                    onMediaClick = onMediaClick,
                    onUserInfoClick = composedStatusInteraction::onUserInfoClick,
                    onVoted = { options ->
                        composedStatusInteraction.onVoted(status.status, options)
                    },
                    onHashtagInStatusClick = { blogAuthor, hashtagInStatus ->
                        composedStatusInteraction.onHashtagInStatusClick(blogAuthor, hashtagInStatus)
                    },
                    onMentionClick = { blogAuthor, mention ->
                        composedStatusInteraction.onMentionClick(blogAuthor, mention)
                    },
                )
            }
        }
    }
}

interface ComposedStatusInteraction {

    fun onStatusInteractive(status: Status, interaction: StatusUiInteraction)
    fun onUserInfoClick(blogAuthor: BlogAuthor)
    fun onVoted(status: Status, blogPollOptions: List<BlogPoll.Option>)
    fun onHashtagInStatusClick(blogAuthor: BlogAuthor, hashtagInStatus: HashtagInStatus)
    fun onHashtagClick(tag: Hashtag)
    fun onMentionClick(blogAuthor: BlogAuthor, mention: Mention)
    fun onStatusClick(status: Status)
    fun onFollowClick(target: BlogAuthor)
    fun onUnfollowClick(target: BlogAuthor)
}
