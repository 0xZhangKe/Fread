package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_notification_favourited_desc
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import org.jetbrains.compose.resources.stringResource

@Composable
fun FavouriteNotification(
    notification: StatusNotification.Like,
    indexInList: Int,
    style: NotificationStyle,
    composedStatusInteraction: ComposedStatusInteraction,
) {
    val blog = notification.blog
    BlogInteractionNotification(
        blog = blog,
        author = notification.author,
        locator = notification.locator,
        icon = Icons.Default.Favorite,
        iconTint = MaterialTheme.colorScheme.tertiary,
        interactionDesc = stringResource(Res.string.shared_notification_favourited_desc),
        indexInList = indexInList,
        style = style,
        composedStatusInteraction = composedStatusInteraction,
    )
}
