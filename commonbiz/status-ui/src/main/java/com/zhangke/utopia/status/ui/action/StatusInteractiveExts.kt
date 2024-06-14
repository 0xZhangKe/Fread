package com.zhangke.utopia.status.ui.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.statusui.R

val StatusUiInteraction.actionName: String
    @Composable get() = when (this) {
        is StatusUiInteraction.Like -> stringResource(R.string.status_ui_like)
        is StatusUiInteraction.Forward -> stringResource(R.string.status_ui_forward)
        is StatusUiInteraction.Comment -> stringResource(R.string.status_ui_comment)
        is StatusUiInteraction.Bookmark -> stringResource(R.string.status_ui_bookmark)
        is StatusUiInteraction.Delete -> stringResource(R.string.status_ui_delete)
        is StatusUiInteraction.Share -> stringResource(R.string.status_ui_share)
    }

val StatusUiInteraction.logo: ImageVector
    @Composable get() = when (this) {
        is StatusUiInteraction.Like -> if (interaction.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
        is StatusUiInteraction.Forward -> Icons.Default.SwapHoriz
        is StatusUiInteraction.Comment -> Icons.Default.ChatBubbleOutline
        is StatusUiInteraction.Bookmark -> if (interaction.bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder
        is StatusUiInteraction.Delete -> Icons.Default.Delete
        is StatusUiInteraction.Share -> ImageVector.vectorResource(R.drawable.ic_share)
    }
