package com.zhangke.fread.status.ui.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.zhangke.framework.utils.formatToHumanReadable
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.ic_format_quote
import com.zhangke.fread.statusui.ic_share
import com.zhangke.fread.statusui.ic_status_comment
import com.zhangke.fread.statusui.ic_status_forward
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun likeIcon(liked: Boolean): ImageVector {
    return if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
}

@Composable
internal fun forwardIcon(): ImageVector {
    return vectorResource(Res.drawable.ic_status_forward)
}

@Composable
fun quoteIcon(): ImageVector {
    return vectorResource(Res.drawable.ic_format_quote)
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
fun pinIcon(pinned: Boolean): ImageVector {
    return if (pinned) Icons.Default.PushPin else Icons.Outlined.PushPin
}

@Composable
internal fun editIcon(): ImageVector {
    return Icons.Default.Edit
}

@Composable
fun likeAlt(): String {
    return stringResource(LocalizedString.statusUiLike)
}

@Composable
internal fun forwardAlt(): String {
    return stringResource(LocalizedString.statusUiBoosted)
}

@Composable
internal fun quoteAlt(): String {
    return stringResource(LocalizedString.statusUiQuote)
}

@Composable
internal fun replyAlt(): String {
    return stringResource(LocalizedString.statusUiComment)
}

@Composable
internal fun bookmarkAlt(bookmarked: Boolean): String {
    return if (bookmarked) stringResource(LocalizedString.statusUiUnbookmark) else stringResource(
        LocalizedString.statusUiBookmark
    )
}

@Composable
internal fun deleteAlt(): String {
    return stringResource(LocalizedString.statusUiDelete)
}

@Composable
internal fun shareAlt(): String {
    return stringResource(LocalizedString.statusUiShare)
}

@Composable
fun pinAlt(pinned: Boolean): String {
    return if (pinned) stringResource(LocalizedString.statusUiUnpin) else stringResource(
        LocalizedString.statusUiPin
    )
}

@Composable
internal fun editAlt(): String {
    return stringResource(LocalizedString.statusUiEdit)
}

internal fun Long.countToLabel(): String? {
    return when {
        this <= 0 -> null
        else -> this.formatToHumanReadable()
    }
}
