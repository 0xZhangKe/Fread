package com.zhangke.utopia.status.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.horizontalPadding
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.statusui.R

@Composable
fun ReblogUi(
    modifier: Modifier = Modifier,
    reblog: Status.Reblog,
    displayTime: String,
    indexInList: Int,
    style: StatusStyle,
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    onInteractive: (StatusUiInteraction) -> Unit,
    onMediaClick: OnBlogMediaClick,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onVoted: (List<BlogPoll.Option>) -> Unit,
    onHashtagInStatusClick: (BlogAuthor, HashtagInStatus) -> Unit,
    onMentionClick: (BlogAuthor, Mention) -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalPadding(style.containerPaddings),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(start = 6.dp),
                text = reblog.author.name,
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.status_ui_forward),
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        BlogUi(
            modifier = Modifier,
            blog = reblog.reblog,
            displayTime = displayTime,
            bottomPanelInteractions = bottomPanelInteractions,
            moreInteractions = moreInteractions,
            indexInList = indexInList,
            style = style,
            reblogAuthor = reblog.author,
            onInteractive = onInteractive,
            onMediaClick = onMediaClick,
            onUserInfoClick = onUserInfoClick,
            onVoted = onVoted,
            onHashtagInStatusClick = onHashtagInStatusClick,
            onMentionClick = onMentionClick,
        )
    }
}
