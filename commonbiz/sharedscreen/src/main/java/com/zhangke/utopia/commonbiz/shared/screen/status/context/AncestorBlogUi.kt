package com.zhangke.utopia.commonbiz.shared.screen.status.context

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.ui.BlogAuthorAvatar
import com.zhangke.utopia.status.ui.BlogContent
import com.zhangke.utopia.status.ui.BlogDivider
import com.zhangke.utopia.status.ui.action.StatusBottomInteractionPanel
import com.zhangke.utopia.status.ui.action.StatusMoreInteractionIcon
import com.zhangke.utopia.status.ui.formatStatusDateTime
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle
import com.zhangke.utopia.status.ui.threads.StatusThread

@Composable
fun AncestorBlogUi(
    modifier: Modifier,
    blog: Blog,
    isFirst: Boolean,
    style: StatusStyle = defaultStatusStyle(),
    indexInList: Int,
    bottomPanelInteractions: List<StatusUiInteraction>,
    moreInteractions: List<StatusUiInteraction>,
    reblogAuthor: BlogAuthor? = null,
    onInteractive: (StatusUiInteraction) -> Unit,
    onMediaClick: OnBlogMediaClick,
) {
    Surface(modifier = modifier) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (
                avatar,
                upThread,
                downThread,
                name,
                guideline,
                dateTime,
                userId,
                moreOptions,
                blogContent,
            ) = createRefs()

            val statusInfoStyle = style.statusInfoStyle
            BlogAuthorAvatar(
                modifier = Modifier
                    .size(statusInfoStyle.avatarSize)
                    .constrainAs(avatar) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    },
                reblogAvatar = reblogAuthor?.avatar,
                authorAvatar = blog.author.avatar,
            )

            if (isFirst) {
                Box(modifier = Modifier.constrainAs(upThread) {
                    start.linkTo(avatar.end)
                    top.linkTo(avatar.top)
                })
            } else {
                StatusThread(
                    modifier = Modifier.constrainAs(upThread) {
                        start.linkTo(avatar.start)
                        end.linkTo(avatar.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(avatar.top)
                        height = Dimension.fillToConstraints
                    },
                )
            }

            StatusThread(modifier = Modifier.constrainAs(downThread) {
                start.linkTo(avatar.start)
                end.linkTo(avatar.end)
                top.linkTo(parent.bottom)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
            })

            Spacer(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .constrainAs(guideline) {
                        top.linkTo(avatar.top)
                        bottom.linkTo(avatar.bottom)
                        start.linkTo(parent.start)
                    }
            )

            Text(
                modifier = Modifier.constrainAs(name) {
                    start.linkTo(avatar.end, margin = statusInfoStyle.avatarToNamePadding)
                    bottom.linkTo(guideline.top)
                },
                text = blog.author.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                modifier = Modifier.constrainAs(dateTime) {
                    start.linkTo(name.start)
                    top.linkTo(name.bottom, statusInfoStyle.nameToTimePadding)
                },
                text = remember(blog.date) { formatStatusDateTime(blog.date) },
                style = statusInfoStyle.descStyle,
            )

            Text(
                modifier = Modifier.constrainAs(userId) {
                    baseline.linkTo(dateTime.baseline)
                    start.linkTo(dateTime.end, statusInfoStyle.timeToIdPadding)
                },
                text = blog.author.webFinger.toString(),
                style = statusInfoStyle.descStyle,
            )
            StatusMoreInteractionIcon(
                modifier = Modifier.constrainAs(moreOptions) {
                    end.linkTo(parent.end)
                    top.linkTo(name.top)
                },
                moreActionList = moreInteractions,
                onActionClick = onInteractive,
            )

            BlogContent(
                modifier = Modifier.constrainAs(blogContent) {
                    start.linkTo(name.start)
                    end.linkTo(parent.end)
                    top.linkTo(dateTime.bottom)
                    width = Dimension.fillToConstraints
                },
                blog = blog,
                style = style.blogStyle,
                indexOfFeeds = indexInList,
                onMediaClick = onMediaClick,
            )
            StatusBottomInteractionPanel(
                modifier = Modifier
                    .fillMaxWidth(),
                interactions = bottomPanelInteractions,
                onInteractive = onInteractive,
            )
            BlogDivider()
        }
    }
}
