package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.fread.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_favourited_desc
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_new_status_desc
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_poll_desc
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_quote_desc
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_reply_desc
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_update_desc
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.action.quoteIcon
import com.zhangke.fread.status.ui.style.LocalStatusUiConfig
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.ic_status_forward
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun StatusNotificationUi(
    modifier: Modifier,
    notification: StatusNotification,
    indexInList: Int,
    style: NotificationStyle = defaultNotificationStyle(),
    onRejectClick: (BlogAuthor) -> Unit,
    onAcceptClick: (BlogAuthor) -> Unit,
    onUnblockClick: (PlatformLocator, BlogAuthor) -> Unit,
    onCancelFollowRequestClick: (PlatformLocator, BlogAuthor) -> Unit,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier) {
            when (notification) {
                is StatusNotification.Like -> {
                    NotificationWithWholeStatus(
                        blog = notification.blog,
                        locator = notification.locator,
                        author = notification.author,
                        createAt = notification.formattingDisplayTime,
                        indexInList = indexInList,
                        icon = Icons.Default.Favorite,
                        iconTint = MaterialTheme.colorScheme.tertiary,
                        interactionDesc = stringResource(Res.string.shared_notification_favourited_desc),
                        style = style,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.Mention -> {
                    FeedsStatusNode(
                        modifier = Modifier.fillMaxWidth(),
                        status = notification.status,
                        indexInList = indexInList,
                        style = style.statusStyle,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.Reply -> {
                    FeedsStatusNode(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(style.containerPaddings),
                        status = notification.status,
                        indexInList = indexInList,
                        style = style.statusStyle,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.Repost -> {
                    NotificationWithWholeStatus(
                        blog = notification.blog,
                        locator = notification.locator,
                        author = notification.author,
                        createAt = notification.formattingDisplayTime,
                        indexInList = indexInList,
                        icon = vectorResource(com.zhangke.fread.statusui.Res.drawable.ic_status_forward),
                        interactionDesc = stringResource(Res.string.shared_notification_reply_desc),
                        style = style,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.Poll -> {
                    NotificationWithWholeStatus(
                        blog = notification.blog,
                        locator = notification.locator,
                        createAt = notification.formattingDisplayTime,
                        author = null,
                        indexInList = indexInList,
                        icon = Icons.Default.Poll,
                        interactionDesc = stringResource(Res.string.shared_notification_poll_desc),
                        style = style,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.Follow -> {
                    FollowNotification(
                        notification = notification,
                        style = style,
                        onUserInfoClick = {
                            composedStatusInteraction.onUserInfoClick(notification.locator, it)
                        },
                        onFollowAccountClick = {
                            composedStatusInteraction.onFollowClick(notification.locator, it)
                        },
                        onUnblockClick = { onUnblockClick(notification.locator, it) },
                        onUnfollowAccountClick = {
                            composedStatusInteraction.onUnfollowClick(notification.locator, it)
                        },
                        onCancelFollowRequestClick = {
                            onCancelFollowRequestClick(notification.locator, it)
                        },
                    )
                }

                is StatusNotification.Update -> {
                    NotificationWithWholeStatus(
                        blog = notification.status.status.intrinsicBlog,
                        locator = notification.locator,
                        author = notification.status.status.triggerAuthor,
                        createAt = notification.formattingDisplayTime,
                        indexInList = indexInList,
                        icon = Icons.Default.Edit,
                        interactionDesc = stringResource(Res.string.shared_notification_update_desc),
                        style = style,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.FollowRequest -> {
                    FollowRequestNotification(
                        notification = notification,
                        style = style,
                        onUserInfoClick = {
                            composedStatusInteraction.onUserInfoClick(notification.locator, it)
                        },
                        onRejectClick = onRejectClick,
                        onAcceptClick = onAcceptClick,
                    )
                }

                is StatusNotification.NewStatus -> {
                    NotificationWithWholeStatus(
                        blog = notification.status.status.intrinsicBlog,
                        locator = notification.locator,
                        author = notification.status.status.triggerAuthor,
                        createAt = notification.formattingDisplayTime,
                        indexInList = indexInList,
                        icon = Icons.Default.NotificationsNone,
                        interactionDesc = stringResource(Res.string.shared_notification_new_status_desc),
                        style = style,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.Quote -> {
                    NotificationWithWholeStatus(
                        blog = notification.status.status.intrinsicBlog,
                        locator = notification.locator,
                        author = notification.status.status.triggerAuthor,
                        createAt = notification.formattingDisplayTime,
                        indexInList = indexInList,
                        icon = quoteIcon(),
                        interactionDesc = stringResource(Res.string.shared_notification_quote_desc),
                        style = style,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.SeveredRelationships -> {
                    SeveredRelationshipsNotification(
                        notification = notification,
                        style = style,
                        onUserInfoClick = {
                            composedStatusInteraction.onUserInfoClick(notification.locator, it)
                        },
                    )
                }

                is StatusNotification.Unknown -> {
                    UnknownNotification(notification = notification)
                }
            }
        }
    }
}

data class NotificationStyle(
    val nameMaxLength: Int,
    /**
     * 通知UI整体的外部边距
     */
    val containerPaddings: PaddingValues,
    /**
     * 通知内部的博文正文和边框之间的边距
     */
    val internalBlogPadding: PaddingValues,

    /**
     * 通知标题到博客正文之间的边距
     */
    val headLineToContentPadding: Dp,

    val statusStyle: StatusStyle,

    /**
     * 通知触发者的头像大小
     */
    val triggerAccountAvatarSize: Dp,

    val typeLogoSize: Dp,
)

object NotificationStyleDefaults {

    const val nameMaxLength = 10

    val containerStartPadding = 16.dp
    val containerTopPadding = 8.dp
    val containerEndPadding = 16.dp
    val containerBottomPadding = 8.dp

    val internalBlogStartPadding = 8.dp
    val internalBlogTopPadding = 8.dp
    val internalBlogEndPadding = 8.dp
    val internalBlogBottomPadding = 8.dp

    val headLineToContentPadding = 6.dp

    val triggerAccountAvatarSize = 26.dp

    val typeLogoSize = 20.dp
}

@Composable
fun defaultNotificationStyle(
    nameMaxLength: Int = NotificationStyleDefaults.nameMaxLength,
    containerPaddings: PaddingValues = PaddingValues(
        start = NotificationStyleDefaults.containerStartPadding,
        top = NotificationStyleDefaults.containerTopPadding,
        end = NotificationStyleDefaults.containerEndPadding,
        bottom = NotificationStyleDefaults.containerBottomPadding,
    ),
    internalBlogPadding: PaddingValues = PaddingValues(
        start = NotificationStyleDefaults.internalBlogStartPadding,
        top = NotificationStyleDefaults.internalBlogTopPadding,
        end = NotificationStyleDefaults.internalBlogEndPadding,
        bottom = NotificationStyleDefaults.internalBlogBottomPadding,
    ),
    headLineToContentPadding: Dp = NotificationStyleDefaults.headLineToContentPadding,
    statusStyle: StatusStyle = LocalStatusUiConfig.current.contentStyle,
    triggerAccountAvatarSize: Dp = NotificationStyleDefaults.triggerAccountAvatarSize,
    typeLogoSize: Dp = NotificationStyleDefaults.typeLogoSize,
) = NotificationStyle(
    nameMaxLength = nameMaxLength,
    containerPaddings = containerPaddings,
    internalBlogPadding = internalBlogPadding,
    headLineToContentPadding = headLineToContentPadding,
    statusStyle = statusStyle,
    triggerAccountAvatarSize = triggerAccountAvatarSize,
    typeLogoSize = typeLogoSize,
)
