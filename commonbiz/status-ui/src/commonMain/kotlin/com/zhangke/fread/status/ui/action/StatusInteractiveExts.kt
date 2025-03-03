package com.zhangke.fread.status.ui.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.FormatQuote
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.zhangke.framework.utils.formatToHumanReadable
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.ic_share
import com.zhangke.fread.statusui.ic_status_comment
import com.zhangke.fread.statusui.ic_status_forward
import com.zhangke.fread.statusui.status_ui_bookmark
import com.zhangke.fread.statusui.status_ui_boosted
import com.zhangke.fread.statusui.status_ui_comment
import com.zhangke.fread.statusui.status_ui_delete
import com.zhangke.fread.statusui.status_ui_edit
import com.zhangke.fread.statusui.status_ui_like
import com.zhangke.fread.statusui.status_ui_pin
import com.zhangke.fread.statusui.status_ui_quote
import com.zhangke.fread.statusui.status_ui_share
import com.zhangke.fread.statusui.status_ui_unbookmark
import com.zhangke.fread.statusui.status_ui_unpin
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
internal fun likeIcon(liked: Boolean): ImageVector {
    return if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
}

@Composable
internal fun forwardIcon(): ImageVector {
    return vectorResource(Res.drawable.ic_status_forward)
}

@Composable
internal fun quoteIcon(): ImageVector {
    return Icons.Outlined.FormatQuote
}

@Composable
fun replyIcon(): ImageVector {
    return vectorResource(Res.drawable.ic_status_comment)
}

@Composable
internal fun bookmarkIcon(bookmarked: Boolean): ImageVector {
    return if (bookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder
}

@Composable
internal fun deleteIcon(): ImageVector {
    return Icons.Default.Delete
}

@Composable
internal fun shareIcon(): ImageVector {
    return vectorResource(Res.drawable.ic_share)
}

@Composable
internal fun pinIcon(pinned: Boolean): ImageVector {
    return if (pinned) Icons.Default.PushPin else Icons.Outlined.PushPin
}

@Composable
internal fun editIcon(): ImageVector {
    return Icons.Default.Edit
}

@Composable
internal fun likeAlt(): String {
    return stringResource(Res.string.status_ui_like)
}

@Composable
internal fun forwardAlt(): String {
    return stringResource(Res.string.status_ui_boosted)
}

@Composable
internal fun quoteAlt(): String {
    return stringResource(Res.string.status_ui_quote)
}

@Composable
internal fun replyAlt(): String {
    return stringResource(Res.string.status_ui_comment)
}

@Composable
internal fun bookmarkAlt(bookmarked: Boolean): String {
    return if (bookmarked) stringResource(Res.string.status_ui_unbookmark) else stringResource(Res.string.status_ui_bookmark)
}

@Composable
internal fun deleteAlt(): String {
    return stringResource(Res.string.status_ui_delete)
}

@Composable
internal fun shareAlt(): String {
    return stringResource(Res.string.status_ui_share)
}

@Composable
internal fun pinAlt(pinned: Boolean): String {
    return if (pinned) stringResource(Res.string.status_ui_unpin) else stringResource(Res.string.status_ui_pin)
}

@Composable
internal fun editAlt(): String {
    return stringResource(Res.string.status_ui_edit)
}

internal fun Long.countToLabel(): String? {
    return when {
        this <= 0 -> null
        else -> this.formatToHumanReadable()
    }
}
