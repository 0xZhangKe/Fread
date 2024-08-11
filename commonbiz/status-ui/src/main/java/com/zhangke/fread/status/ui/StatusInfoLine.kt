package com.zhangke.fread.status.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.ui.action.StatusMoreInteractionIcon
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.R
import java.util.Date

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
    editedAt: Date? = null,
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
                .padding(start = style.infoLineStyle.nameToAvatarSpacing, end = 6.dp),
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
                val fontColor = style.secondaryFontColor
                if (editedAt != null) {
                    Text(
                        modifier = Modifier
                            .padding(end = 4.dp),
                        text = stringResource(id = R.string.status_ui_info_label_edited),
                        style = style.infoLineStyle.descStyle,
                        maxLines = 1,
                        color = fontColor,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Text(
                    modifier = Modifier,
                    text = displayTime,
                    style = style.infoLineStyle.descStyle,
                    maxLines = 1,
                    color = fontColor,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    modifier = Modifier.padding(start = 4.dp),
                    text = blogAuthor.webFinger.toString(),
                    style = style.infoLineStyle.descStyle,
                    maxLines = 1,
                    color = fontColor,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        StatusMoreInteractionIcon(
            modifier = Modifier
                .align(Alignment.Top)
                .padding(end = style.containerEndPadding / 2),
            blogUrl = blogUrl,
            style = style,
            moreActionList = moreInteractions,
            onActionClick = onInteractive,
        )
    }
}
