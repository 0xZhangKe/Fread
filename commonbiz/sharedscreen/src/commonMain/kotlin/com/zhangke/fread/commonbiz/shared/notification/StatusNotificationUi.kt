package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.fread.commonbiz.shared.composable.WholeBlogUi
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_new_status_desc
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_quote_desc
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_reblog_desc
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_reply_desc
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_update_desc
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.ui.BlogDivider
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.action.quoteIcon
import com.zhangke.fread.status.ui.action.replyIcon
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
    onRejectClick: (StatusNotification.FollowRequest) -> Unit,
    onAcceptClick: (StatusNotification.FollowRequest) -> Unit,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    Column(modifier = modifier) {
        Box(modifier = Modifier) {
            when (notification) {
                is StatusNotification.Like -> {
                    FavouriteNotification(
                        notification = notification,
                        indexInList = indexInList,
                        style = style,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.Mention -> {
                    WholeBlogUi(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { composedStatusInteraction.onStatusClick(notification.status) },
                        statusUiState = notification.status,
                        indexInList = indexInList,
                        style = style.statusStyle,
                        showDivider = false,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.Reply -> {
                    NotificationWithWholeStatus(
                        status = notification.status,
                        author = notification.status.status.triggerAuthor,
                        indexInList = indexInList,
                        icon = replyIcon(),
                        interactionDesc = stringResource(Res.string.shared_notification_reply_desc),
                        style = style,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.Repost -> {
                    BlogInteractionNotification(
                        blog = notification.blog,
                        locator = notification.locator,
                        author = notification.author,
                        icon = vectorResource(com.zhangke.fread.statusui.Res.drawable.ic_status_forward),
                        interactionDesc = stringResource(Res.string.shared_notification_reblog_desc),
                        indexInList = indexInList,
                        style = style,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.Poll -> {
                    PollNotification(
                        notification = notification,
                        indexInList = indexInList,
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
                    )
                }

                is StatusNotification.Update -> {
                    NotificationWithWholeStatus(
                        status = notification.status,
                        author = notification.status.status.triggerAuthor,
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
                        status = notification.status,
                        author = notification.status.status.triggerAuthor,
                        indexInList = indexInList,
                        icon = Icons.Default.NotificationsNone,
                        interactionDesc = stringResource(Res.string.shared_notification_new_status_desc),
                        style = style,
                        composedStatusInteraction = composedStatusInteraction,
                    )
                }

                is StatusNotification.Quote -> {
                    NotificationWithWholeStatus(
                        status = notification.status,
                        author = notification.status.status.triggerAuthor,
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
        val layoutDirection = LocalLayoutDirection.current
        BlogDivider(
            modifier = Modifier.padding(
                start = style.containerPaddings.calculateStartPadding(layoutDirection),
                end = style.containerPaddings.calculateEndPadding(layoutDirection),
            )
        )
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
