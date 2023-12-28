package com.zhangke.utopia.status.ui.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.zhangke.utopia.status.status.model.StatusAction
import com.zhangke.utopia.statusui.R

val StatusAction.actionName: String
    @Composable get() = when (this) {
        is StatusAction.Like -> stringResource(R.string.status_ui_like)
        is StatusAction.Forward -> stringResource(R.string.status_ui_forward)
        is StatusAction.Comment -> stringResource(R.string.status_ui_comment)
        is StatusAction.Bookmark -> stringResource(R.string.status_ui_bookmark)
        is StatusAction.Delete -> stringResource(R.string.status_ui_delete)
    }

val StatusAction.logo: ImageVector
    @Composable get() = when (this) {
        is StatusAction.Like -> if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
        is StatusAction.Forward -> Icons.Default.SwapHoriz
        is StatusAction.Comment -> Icons.Default.ChatBubbleOutline
        is StatusAction.Bookmark -> Icons.Default.Bookmark
        is StatusAction.Delete -> Icons.Default.Delete
    }
