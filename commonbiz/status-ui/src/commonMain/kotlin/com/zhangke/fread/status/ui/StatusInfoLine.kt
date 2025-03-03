package com.zhangke.fread.status.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.StyledTextButton
import com.zhangke.framework.composable.TextButtonStyle
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.ui.action.StatusMoreInteractionIcon
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_follow
import com.zhangke.fread.statusui.status_ui_info_label_edited
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource

/**
 * Status 头部信息行，主要包括头像，
 * 用户名，Handle，时间，更多按钮等。
 */
@Composable
fun StatusInfoLine(
    modifier: Modifier,
    blog: Blog,
    blogTranslationState: BlogTranslationUiState,
    isOwner: Boolean,
    displayTime: String,
    style: StatusStyle,
    visibility: StatusVisibility,
    showFollowButton: Boolean,
    showMoreOperationIcon: Boolean = true,
    onUrlClick: (url: String) -> Unit = {},
    onInteractive: (StatusActionType, Blog) -> Unit = { _, _ -> },
    onUserInfoClick: ((BlogAuthor) -> Unit)? = null,
    onFollowClick: ((BlogAuthor) -> Unit)? = null,
    onTranslateClick: () -> Unit = {},
    reblogAuthor: BlogAuthor? = null,
    editedAt: Instant? = null,
) {
    val blogAuthor = blog.author
    Row(
        modifier = modifier.padding(start = style.containerStartPadding),
    ) {
        BlogAuthorAvatar(
            modifier = Modifier
                .size(style.infoLineStyle.avatarSize),
            onClick = {
                reportClick(StatusDataElements.USER_INFO)
                onUserInfoClick?.invoke(blogAuthor)
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
                    .clickable(enabled = onUserInfoClick != null) {
                        reportClick(StatusDataElements.USER_INFO)
                        onUserInfoClick?.invoke(blogAuthor)
                    },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                richText = blogAuthor.humanizedName,
                onUrlClick = onUrlClick,
                fontWeight = FontWeight.SemiBold,
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
                        text = stringResource(Res.string.status_ui_info_label_edited),
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
                    text = blogAuthor.prettyHandle,
                    style = style.infoLineStyle.descStyle,
                    maxLines = 1,
                    color = fontColor,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        if (showFollowButton) {
            StyledTextButton(
                modifier = Modifier
                    .align(Alignment.Top)
                    .heightIn(min = 20.dp)
                    .padding(end = 4.dp),
                text = stringResource(Res.string.status_ui_follow),
                style = TextButtonStyle.STANDARD,
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                onClick = { onFollowClick?.invoke(blogAuthor) },
            )
        }

        if (showMoreOperationIcon) {
            val moreIconAlign = if (showFollowButton) {
                Alignment.CenterVertically
            } else {
                Alignment.Top
            }
            StatusMoreInteractionIcon(
                modifier = Modifier
                    .align(moreIconAlign)
                    .padding(end = style.containerEndPadding / 2),
                blog = blog,
                isOwner = isOwner,
                blogTranslationState = blogTranslationState,
                style = style,
                onActionClick = onInteractive,
                onTranslateClick = onTranslateClick,
            )
        }
    }
}
