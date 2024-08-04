package com.zhangke.fread.commonbiz.shared.screen.status.context

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zhangke.framework.composable.endPadding
import com.zhangke.framework.composable.startPadding
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.BlogContent
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.action.StatusBottomInteractionPanel
import com.zhangke.fread.status.ui.action.StatusMoreInteractionIcon
import com.zhangke.fread.status.ui.image.OnBlogMediaClick
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.style.defaultStatusStyle
import com.zhangke.fread.status.ui.threads.StatusThread

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
    val context = LocalContext.current
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
                bottomPanelRef,
                bottomPaddingRef,
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
                onClick = {
                    composedStatusInteraction.onUserInfoClick(status.role, blog.author)
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

            FreadRichText(
                modifier = Modifier.constrainAs(name) {
                    start.linkTo(avatar.end, margin = statusInfoStyle.avatarToNamePadding)
                    bottom.linkTo(guideline.top)
                },
                richText = blog.author.humanizedName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSizeSp = style.contentSize.userNameSize.value,
                onUrlClick = {
                    BrowserLauncher.launchWebTabInApp(context, it, status.role)
                },
            )
            Text(
                modifier = Modifier.constrainAs(dateTime) {
                    start.linkTo(name.start)
                    top.linkTo(name.bottom, statusInfoStyle.nameToTimePadding)
                },
                text = displayTime,
                fontSize = style.contentSize.infoSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = statusInfoStyle.descStyle,
            )

            Text(
                modifier = Modifier.constrainAs(userId) {
                    baseline.linkTo(dateTime.baseline)
                    start.linkTo(dateTime.end, statusInfoStyle.timeToIdPadding)
                },
                text = blog.author.webFinger.toString(),
                style = statusInfoStyle.descStyle,
                fontSize = style.contentSize.infoSize,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            StatusMoreInteractionIcon(
                modifier = Modifier.constrainAs(moreOptions) {
                    end.linkTo(parent.end, style.iconEndPadding)
                    top.linkTo(name.top)
                },
                blogUrl = status.status.intrinsicBlog.url,
                moreActionList = status.moreInteractions,
                onActionClick = {
                    composedStatusInteraction.onStatusInteractive(status, it)
                },
            )

            BlogContent(
                modifier = Modifier
                    .endPadding(style.containerPaddings)
                    .constrainAs(blogContent) {
                        start.linkTo(name.start)
                        end.linkTo(parent.end)
                        top.linkTo(dateTime.bottom)
                        width = Dimension.fillToConstraints
                    },
                blog = blog,
                style = style,
                indexOfFeeds = indexInList,
                onMediaClick = onMediaClick,
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
            )
            StatusBottomInteractionPanel(
                modifier = Modifier
                    .constrainAs(bottomPanelRef) {
                        start.linkTo(avatar.end)
                        end.linkTo(parent.end, style.iconEndPadding)
                        top.linkTo(blogContent.bottom)
                        width = Dimension.fillToConstraints
                    },
                interactions = status.bottomInteractions,
                onInteractive = {
                    composedStatusInteraction.onStatusInteractive(status, it)
                },
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(style.containerPaddings.calculateBottomPadding())
                    .constrainAs(bottomPaddingRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(bottomPanelRef.bottom)
                    }
            )
        }
    }
}
