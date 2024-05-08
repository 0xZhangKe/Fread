package com.zhangke.utopia.commonbiz.shared.screen.status.context

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zhangke.framework.composable.endPadding
import com.zhangke.framework.composable.startPadding
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.ui.BlogAuthorAvatar
import com.zhangke.utopia.status.ui.BlogContent
import com.zhangke.utopia.status.ui.ComposedStatusInteraction
import com.zhangke.utopia.status.ui.action.StatusBottomInteractionPanel
import com.zhangke.utopia.status.ui.action.StatusMoreInteractionIcon
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle
import com.zhangke.utopia.status.ui.threads.StatusThread

@Composable
fun AncestorBlogUi(
    modifier: Modifier,
    status: StatusUiState,
    displayTime: String,
    isFirst: Boolean,
    style: StatusStyle = defaultStatusStyle(),
    indexInList: Int,
    composedStatusInteraction: ComposedStatusInteraction,
    onMediaClick: OnBlogMediaClick,
) {
    val blog = status.status.intrinsicBlog
    Surface(modifier = modifier) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .startPadding(style.containerPaddings)
        ) {
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

            val upThreadModifier = Modifier.constrainAs(upThread) {
                start.linkTo(avatar.start)
                end.linkTo(avatar.end)
                top.linkTo(parent.top)
                height = Dimension.value(style.containerPaddings.calculateTopPadding())
            }

            if (isFirst) {
                Box(modifier = upThreadModifier)
            } else {
                StatusThread(modifier = upThreadModifier)
            }

            val statusInfoStyle = style.statusInfoStyle
            BlogAuthorAvatar(
                modifier = Modifier
                    .size(statusInfoStyle.avatarSize)
                    .constrainAs(avatar) {
                        top.linkTo(upThread.bottom, 2.dp)
                        start.linkTo(parent.start)
                    },
                authorAvatar = blog.author.avatar,
                reblogAvatar = null,
            )

            StatusThread(modifier = Modifier.constrainAs(downThread) {
                start.linkTo(avatar.start)
                end.linkTo(avatar.end)
                top.linkTo(avatar.bottom, 2.dp)
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
                text = displayTime,
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
                    end.linkTo(parent.end, style.iconEndPadding)
                    top.linkTo(name.top)
                },
                moreActionList = status.moreInteractions,
                onActionClick = {
                    composedStatusInteraction.onStatusInteractive(status.status, it)
                },
            )

            Column(modifier = Modifier
                .constrainAs(blogContent) {
                    start.linkTo(name.start)
                    end.linkTo(parent.end)
                    top.linkTo(dateTime.bottom)
                    width = Dimension.fillToConstraints
                }) {
                BlogContent(
                    modifier = Modifier.endPadding(style.containerPaddings),
                    blog = blog,
                    style = style.blogStyle,
                    indexOfFeeds = indexInList,
                    onMediaClick = onMediaClick,
                    onVoted = {
                        composedStatusInteraction.onVoted(status.status, it)
                    },
                    onHashtagInStatusClick = composedStatusInteraction::onHashtagInStatusClick,
                    onMentionClick = composedStatusInteraction::onMentionClick,
                )
                StatusBottomInteractionPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .endPadding(style.containerPaddings),
                    interactions = status.bottomInteractions,
                    onInteractive = {
                        composedStatusInteraction.onStatusInteractive(status.status, it)
                    },
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(style.containerPaddings.calculateBottomPadding())
                )
            }
        }
    }
}
