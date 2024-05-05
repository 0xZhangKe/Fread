package com.zhangke.utopia.status.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.image.BlogMediaClickEvent
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

@Composable
fun StatusUi(
    modifier: Modifier = Modifier,
    status: StatusUiState,
    indexInList: Int,
    style: StatusStyle = defaultStatusStyle(),
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
                    composedStatusInteraction = composedStatusInteraction,
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
                    composedStatusInteraction = composedStatusInteraction,
                    style = style,
                )
            }
        }
    }
}


interface ComposedStatusInteraction {

    fun onInteractive(interaction: StatusUiInteraction)
    fun onMediaClick(event: BlogMediaClickEvent)
    fun onUserInfoClick(blogAuthor: BlogAuthor)
    fun onVoted(blogPollOptions: List<BlogPoll.Option>)
    fun onHashtagInStatusClick(blogAuthor: BlogAuthor, hashtagInStatus: HashtagInStatus)
    fun onMentionClick(blogAuthor: BlogAuthor, mention: Mention)
    fun onStatusClick(status: Status)
}
