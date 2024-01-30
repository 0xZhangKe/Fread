package com.zhangke.utopia.status.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.imageLoader
import com.zhangke.framework.composable.startPadding
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.ui.action.StatusMoreInteractionIcon
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.threads.StatusThread

/**
 * Status 头部信息行，主要包括头像，
 * 用户名，WebFinger，时间，更多按钮等。
 */
@Composable
fun StatusInfoLine(
    modifier: Modifier,
    blogAuthor: BlogAuthor,
    displayTime: String,
    style: StatusStyle,
    showUpThread: Boolean = false,
    showDownThread: Boolean = false,
    moreInteractions: List<StatusUiInteraction>,
    onInteractive: (StatusUiInteraction) -> Unit,
    onUserInfoClick: (BlogAuthor) -> Unit,
    reblogAuthor: BlogAuthor? = null,
) {
    val infoStyle = style.statusInfoStyle
    ConstraintLayout(modifier = modifier.startPadding(style.containerPaddings)) {
        val (
            avatar,
            upThread,
            downThread,
            name,
            guideline,
            dateTime,
            userId,
            moreOptions,
        ) = createRefs()

        if (showUpThread) {
            StatusThread(
                modifier = Modifier.constrainAs(upThread) {
                    start.linkTo(avatar.start)
                    end.linkTo(avatar.end)
                    top.linkTo(parent.top)
                    height = Dimension.value(style.containerPaddings.calculateTopPadding())
                },
            )
        } else {
            Box(modifier = Modifier.constrainAs(upThread) {
                start.linkTo(avatar.end)
                top.linkTo(parent.top)
                height = Dimension.value(style.containerPaddings.calculateTopPadding())
            })
        }

        BlogAuthorAvatar(
            modifier = Modifier
                .size(infoStyle.avatarSize)
                .clickable {
                    onUserInfoClick(blogAuthor)
                }
                .constrainAs(avatar) {
                    top.linkTo(upThread.bottom, 2.dp)
                    start.linkTo(parent.start)
                },
            reblogAvatar = reblogAuthor?.avatar,
            authorAvatar = blogAuthor.avatar,
        )

        if (showDownThread) {
            StatusThread(modifier = Modifier.constrainAs(downThread) {
                start.linkTo(avatar.start)
                end.linkTo(avatar.end)
                top.linkTo(parent.bottom)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
            })
        } else {
            Box(modifier = Modifier.constrainAs(downThread) {
                start.linkTo(avatar.end)
                top.linkTo(avatar.top)
            })
        }

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
            modifier = Modifier
                .clickable {
                    onUserInfoClick(blogAuthor)
                }
                .constrainAs(name) {
                    start.linkTo(avatar.end, margin = infoStyle.avatarToNamePadding)
                    bottom.linkTo(guideline.top)
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            text = blogAuthor.name,
            style = infoStyle.nameStyle,
        )
        Text(
            modifier = Modifier.constrainAs(dateTime) {
                start.linkTo(name.start)
                top.linkTo(name.bottom, infoStyle.nameToTimePadding)
            },
            text = displayTime,
            style = infoStyle.descStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        Text(
            modifier = Modifier
                .clickable {
                    onUserInfoClick(blogAuthor)
                }
                .constrainAs(userId) {
                    baseline.linkTo(dateTime.baseline)
                    start.linkTo(dateTime.end, infoStyle.timeToIdPadding)
                },
            text = blogAuthor.webFinger.toString(),
            style = infoStyle.descStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        StatusMoreInteractionIcon(
            modifier = Modifier.constrainAs(moreOptions) {
                end.linkTo(parent.end, style.iconEndPadding)
                top.linkTo(name.top)
            },
            moreActionList = moreInteractions,
            onActionClick = onInteractive,
        )
    }
}

@Composable
fun BlogAuthorAvatar(
    modifier: Modifier,
    reblogAvatar: String?,
    authorAvatar: String?,
) {
    if (reblogAvatar.isNullOrEmpty()) {
        BlogAuthorAvatar(
            modifier = modifier,
            imageUrl = authorAvatar,
        )
    } else {
        Box(modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 6.dp, bottom = 6.dp)
            ) {
                BlogAuthorAvatar(
                    modifier = Modifier.fillMaxSize(),
                    imageUrl = authorAvatar,
                )
            }
            BlogAuthorAvatar(
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.BottomEnd),
                imageUrl = reblogAvatar,
            )
        }
    }
}

@Composable
fun BlogAuthorAvatar(
    modifier: Modifier,
    imageUrl: String?,
) {
    AsyncImage(
        modifier = modifier.clip(CircleShape),
        model = imageUrl,
        imageLoader = LocalContext.current.imageLoader,
        error = rememberVectorPainter(Icons.Default.AccountCircle),
        placeholder = rememberVectorPainter(Icons.Default.AccountCircle),
        contentDescription = null,
    )
}
