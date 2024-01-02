package com.zhangke.utopia.commonbiz.shared.screen.status.context

import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import java.util.Date

@Composable
fun AnchorBlogUi(
    modifier: Modifier,
    blog: Blog,
    indexInList: Int,
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    reblogAuthor: BlogAuthor? = null,
    onInteractive: (StatusUiInteraction) -> Unit,
    onMediaClick: OnBlogMediaClick,
) {

    Divider()
}

@Composable
fun AnchorBlogInfoLine(
    modifier: Modifier,
    blogAuthor: BlogAuthor,
    lastEditTime: Date,
    moreInteractions: List<StatusUiInteraction>,
    onInteractive: (StatusUiInteraction) -> Unit,
    reblogAuthor: BlogAuthor? = null,
){

}
