package com.zhangke.utopia.activitypub.app.internal.composable.notifications

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotification
import com.zhangke.utopia.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.utopia.activitypub.app.internal.screen.notifications.NotificationUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.ui.BlogDivider
import com.zhangke.utopia.status.ui.style.StatusStyle
import com.zhangke.utopia.status.ui.style.defaultStatusStyle

@Composable
fun StatusNotificationUi(
    modifier: Modifier,
    notification: NotificationUiState,
    indexInList: Int,
    style: NotificationStyle = defaultNotificationStyle(),
    onInteractive: (StatusUiState, StatusUiInteraction) -> Unit,
    onRejectClick: (NotificationUiState) -> Unit,
    onAcceptClick: (NotificationUiState) -> Unit,
) {
    if (notification.type == StatusNotificationType.MENTION) {
        MentionNotification(
            notification = notification,
            indexInList = indexInList,
            onInteractive = onInteractive,
            style = style,
        )
        return
    }
    Column(modifier = modifier) {
        Box(modifier = Modifier.padding(style.containerPaddings)) {
            when (notification.type) {
                StatusNotificationType.FAVOURITE -> {
                    FavouriteNotification(
                        notification = notification,
                        indexInList = indexInList,
                        style = style,
                    )
                }

                StatusNotificationType.REBLOG -> {
                    ReblogNotification(
                        notification = notification,
                        indexInList = indexInList,
                        style = style,
                    )
                }

                StatusNotificationType.POLL -> {
                    PollNotification(
                        notification = notification,
                        indexInList = indexInList,
                        style = style,
                    )
                }

                StatusNotificationType.FOLLOW -> {
                    FollowNotification(
                        notification = notification,
                        style = style,
                    )
                }

                StatusNotificationType.UPDATE -> {
                    UpdateNotification(
                        notification = notification,
                        indexInList = indexInList,
                        style = style,
                        onInteractive = onInteractive,
                    )
                }

                StatusNotificationType.FOLLOW_REQUEST -> {
                    FollowRequestNotification(
                        notification = notification,
                        style = style,
                        onRejectClick = onRejectClick,
                        onAcceptClick = onAcceptClick,
                    )
                }

                StatusNotificationType.STATUS -> {
                    NewStatusNotification(
                        notification = notification,
                        indexInList = indexInList,
                        style = style,
                        onInteractive = onInteractive,
                    )
                }

                StatusNotificationType.SEVERED_RELATIONSHIPS -> {
                    SeveredRelationshipsNotification(
                        notification = notification,
                        style = style,
                    )
                }

                else -> {
                    UnknownNotification(
                        notification = notification,
                    )
                }
            }
        }
        BlogDivider()
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
    statusStyle: StatusStyle = defaultStatusStyle(),
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
