package com.zhangke.utopia.status.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.ui.action.StatusBottomInteractionPanel
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick

@Composable
fun BlogUi(
    modifier: Modifier,
    blog: Blog,
    indexInList: Int,
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    reblogAuthor: BlogAuthor? = null,
    onInteractive: (StatusUiInteraction) -> Unit,
    onMediaClick: OnBlogMediaClick,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        StatusInfoLine(
            modifier = Modifier.fillMaxWidth(),
            blogAuthor = blog.author,
            lastEditTime = blog.date,
            moreInteractions = moreInteractions,
            onInteractive = onInteractive,
            reblogAuthor = reblogAuthor,
        )
        BlogContent(
            modifier = Modifier.fillMaxWidth(),
            blog = blog,
            indexOfFeeds = indexInList,
            onMediaClick = onMediaClick,
        )
        StatusBottomInteractionPanel(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
            interactions = bottomPanelInteractions,
            onInteractive = onInteractive,
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
        )
    }
}
