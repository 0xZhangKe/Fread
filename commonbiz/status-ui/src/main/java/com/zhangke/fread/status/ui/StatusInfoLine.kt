package com.zhangke.fread.status.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.imageLoader
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.ui.action.StatusMoreInteractionIcon
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.status.ui.threads.StatusThread

/**
 * Status 头部信息行，主要包括头像，
 * 用户名，WebFinger，时间，更多按钮等。
 */

@Composable
fun StatusInfoLine(
    modifier: Modifier,
    blogAuthor: BlogAuthor,
    blogUrl: String,
    displayTime: String,
    style: StatusStyle,
    visibility: StatusVisibility,
    moreInteractions: List<StatusUiInteraction>,
    onInteractive: (StatusUiInteraction) -> Unit,
    onUserInfoClick: (BlogAuthor) -> Unit,
    onUrlClick: (url: String) -> Unit,
    reblogAuthor: BlogAuthor? = null,
) {
    Row(
        modifier = modifier.padding(start = style.containerStartPadding),
    ) {
        BlogAuthorAvatar(
            modifier = Modifier
                .size(style.infoLineStyle.avatarSize)
                .clickable {
                    reportClick(StatusDataElements.USER_INFO)
                    onUserInfoClick(blogAuthor)
                },
            onClick = {
                reportClick(StatusDataElements.USER_INFO)
                onUserInfoClick(blogAuthor)
            },
            reblogAvatar = reblogAuthor?.avatar,
            authorAvatar = blogAuthor.avatar,
        )
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(start = 8.dp, end = 6.dp),
        ) {
            FreadRichText(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        reportClick(StatusDataElements.USER_INFO)
                        onUserInfoClick(blogAuthor)
                    },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                richText = blogAuthor.humanizedName,
                onUrlClick = onUrlClick,
                fontSizeSp = style.infoLineStyle.nameSize.value,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (visibility == StatusVisibility.PRIVATE ||
                    visibility == StatusVisibility.UNLISTED ||
                    visibility == StatusVisibility.DIRECT
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(14.dp),
                        imageVector = if (visibility == StatusVisibility.UNLISTED) {
                            Icons.Default.LockOpen
                        } else {
                            Icons.Default.Lock
                        },
                        contentDescription = null,
                    )
                }
                Text(
                    modifier = Modifier,
                    text = displayTime,
                    style = style.infoLineStyle.descStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = blogAuthor.webFinger.toString(),
                    style = style.infoLineStyle.descStyle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        StatusMoreInteractionIcon(
            modifier = Modifier.padding(end = style.containerEndPadding / 2),
            blogUrl = blogUrl,
            moreActionList = moreInteractions,
            onActionClick = onInteractive,
        )
    }
}


//@Composable
//fun StatusInfoLineOld(
//    modifier: Modifier,
//    blogAuthor: BlogAuthor,
//    blogUrl: String,
//    displayTime: String,
//    style: StatusStyle,
//    visibility: StatusVisibility,
//    showUpThread: Boolean = false,
//    showDownThread: Boolean = false,
//    moreInteractions: List<StatusUiInteraction>,
//    onInteractive: (StatusUiInteraction) -> Unit,
//    onUserInfoClick: (BlogAuthor) -> Unit,
//    onUrlClick: (url: String) -> Unit,
//    reblogAuthor: BlogAuthor? = null,
//) {
//    val infoStyle = style.statusInfoStyle
//    ConstraintLayout(modifier = modifier.padding(start = style.containerStartPadding)) {
//        val (
//            avatar,
//            upThread,
//            downThread,
//            name,
//            guideline,
//            visibilityIconRef,
//            dateTime,
//            userId,
//            moreOptions,
//        ) = createRefs()
//
//        if (showUpThread) {
//            StatusThread(
//                modifier = Modifier.constrainAs(upThread) {
//                    start.linkTo(avatar.start)
//                    end.linkTo(avatar.end)
//                    top.linkTo(parent.top)
//                    height = Dimension.value(style.containerTopPadding)
//                },
//            )
//        } else {
//            Box(modifier = Modifier.constrainAs(upThread) {
//                start.linkTo(avatar.end)
//                top.linkTo(parent.top)
//                height = Dimension.value(style.containerTopPadding)
//            })
//        }
//
//        BlogAuthorAvatar(
//            modifier = Modifier
//                .clickable {
//                    reportClick(StatusDataElements.USER_INFO)
//                    onUserInfoClick(blogAuthor)
//                }
//                .size(infoStyle.avatarSize)
//                .constrainAs(avatar) {
//                    top.linkTo(upThread.bottom, 2.dp)
//                    start.linkTo(parent.start)
//                },
//            onClick = {
//                reportClick(StatusDataElements.USER_INFO)
//                onUserInfoClick(blogAuthor)
//            },
//            reblogAvatar = reblogAuthor?.avatar,
//            authorAvatar = blogAuthor.avatar,
//        )
//
//        if (showDownThread) {
//            StatusThread(modifier = Modifier.constrainAs(downThread) {
//                start.linkTo(avatar.start)
//                end.linkTo(avatar.end)
//                top.linkTo(parent.bottom)
//                bottom.linkTo(parent.bottom)
//                height = Dimension.fillToConstraints
//            })
//        } else {
//            Box(modifier = Modifier.constrainAs(downThread) {
//                start.linkTo(avatar.end)
//                top.linkTo(avatar.top)
//            })
//        }
//
//        Spacer(
//            modifier = Modifier
//                .height(1.dp)
//                .fillMaxWidth()
//                .constrainAs(guideline) {
//                    top.linkTo(avatar.top)
//                    bottom.linkTo(avatar.bottom)
//                    start.linkTo(parent.start)
//                }
//        )
//
//        FreadRichText(
//            modifier = Modifier
//                .clickable {
//                    reportClick(StatusDataElements.USER_INFO)
//                    onUserInfoClick(blogAuthor)
//                }
//                .constrainAs(name) {
//                    start.linkTo(avatar.end, infoStyle.avatarToNamePadding)
//                    end.linkTo(moreOptions.start, 2.dp)
//                    bottom.linkTo(guideline.top)
//                    width = Dimension.fillToConstraints
//                },
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            richText = blogAuthor.humanizedName,
//            onUrlClick = onUrlClick,
//            fontSizeSp = style.contentSize.userNameSize.value,
//        )
//
//        Box(
//            modifier = Modifier.constrainAs(visibilityIconRef) {
//                start.linkTo(name.start)
//                top.linkTo(dateTime.top)
//                bottom.linkTo(dateTime.bottom)
//            },
//        ) {
//            if (visibility == StatusVisibility.PRIVATE ||
//                visibility == StatusVisibility.UNLISTED ||
//                visibility == StatusVisibility.DIRECT
//            ) {
//                Icon(
//                    modifier = Modifier
//                        .padding(end = 4.dp)
//                        .size(14.dp),
//                    imageVector = if (visibility == StatusVisibility.UNLISTED) {
//                        Icons.Default.LockOpen
//                    } else {
//                        Icons.Default.Lock
//                    },
//                    contentDescription = null,
//                )
//            }
//        }
//
//        Text(
//            modifier = Modifier.constrainAs(dateTime) {
//                start.linkTo(visibilityIconRef.end)
//                top.linkTo(name.bottom, infoStyle.nameToInfoLineSpacing)
//            },
//            text = displayTime,
//            style = infoStyle.descStyle,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            fontSize = style.contentSize.infoSize,
//        )
//
//        Text(
//            modifier = Modifier
//                .constrainAs(userId) {
//                    baseline.linkTo(dateTime.baseline)
//                    start.linkTo(dateTime.end, 2.dp)
//                    end.linkTo(moreOptions.start, 8.dp)
//                    width = Dimension.fillToConstraints
//                },
//            text = blogAuthor.webFinger.toString(),
//            style = infoStyle.descStyle,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            fontSize = style.contentSize.infoSize,
//        )
//        StatusMoreInteractionIcon(
//            modifier = Modifier.constrainAs(moreOptions) {
//                end.linkTo(parent.end, style.iconEndPadding)
//                top.linkTo(parent.top)
//            },
//            blogUrl = blogUrl,
//            moreActionList = moreInteractions,
//            onActionClick = onInteractive,
//        )
//    }
//}
