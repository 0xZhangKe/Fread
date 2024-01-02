package com.zhangke.utopia.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.ui.action.StatusMoreInteractionIcon
import com.zhangke.utopia.status.ui.style.StatusInfoStyle
import com.zhangke.utopia.status.ui.threads.StatusThread
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.getOrSet

/**
 * Status 头部信息行，主要包括头像，
 * 用户名，WebFinger，时间，更多按钮等。
 */
@Composable
fun StatusInfoLine(
    modifier: Modifier,
    blogAuthor: BlogAuthor,
    lastEditTime: Date,
    style: StatusInfoStyle,
    showUpThread: Boolean = false,
    showDownThread: Boolean = false,
    moreInteractions: List<StatusUiInteraction>,
    onInteractive: (StatusUiInteraction) -> Unit,
    reblogAuthor: BlogAuthor? = null,
) {
    ConstraintLayout(modifier = modifier) {
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

        BlogAuthorAvatar(
            modifier = Modifier
                .size(style.avatarSize)
                .constrainAs(avatar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                },
            reblogAvatar = reblogAuthor?.avatar,
            authorAvatar = blogAuthor.avatar,
        )

        if (showUpThread) {
            StatusThread(
                modifier = Modifier.constrainAs(upThread) {
                    start.linkTo(avatar.start)
                    end.linkTo(avatar.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(avatar.top)
                    height = Dimension.fillToConstraints
                },
            )
        } else {
            Box(modifier = Modifier.constrainAs(upThread) {
                start.linkTo(avatar.end)
                top.linkTo(avatar.top)
            })
        }
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
            modifier = Modifier.constrainAs(name) {
                start.linkTo(avatar.end, margin = style.avatarToNamePadding)
                bottom.linkTo(guideline.top)
            },
            text = blogAuthor.name,
            style = style.nameStyle,
        )
        Text(
            modifier = Modifier.constrainAs(dateTime) {
                start.linkTo(name.start)
                top.linkTo(name.bottom, style.nameToTimePadding)
            },
            text = remember(lastEditTime) { formatStatusDateTime(lastEditTime) },
            style = style.descStyle,
        )

        Text(
            modifier = Modifier.constrainAs(userId) {
                baseline.linkTo(dateTime.baseline)
                start.linkTo(dateTime.end, style.timeToIdPadding)
            },
            text = blogAuthor.webFinger.toString(),
            style = style.descStyle,
        )
        StatusMoreInteractionIcon(
            modifier = Modifier.constrainAs(moreOptions) {
                end.linkTo(parent.end)
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
private fun BlogAuthorAvatar(
    modifier: Modifier,
    imageUrl: String?,
) {
    AsyncImage(
        modifier = modifier
            .clip(CircleShape),
        model = imageUrl,
//        error = Icons.Default.AccountCircle,
//        placeholder = Icons.Default.AccountCircle,
        contentDescription = null,
    )
}

private val statusDateFormatLocal = ThreadLocal<SimpleDateFormat>()

fun formatStatusDateTime(date: Date): String {
    val format = statusDateFormatLocal.getOrSet { SimpleDateFormat("MM-dd HH:mm:ss", Locale.ROOT) }
    return format.format(date)
}
