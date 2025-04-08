package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia

@Composable
fun PublishPostMediaAttachment(
    modifier: Modifier,
    media: PublishPostMediaAttachment,
    mediaAltMaxCharacters: Int,
    onAltChanged: (PublishPostMedia, String) -> Unit,
    onDeleteClick: (PublishPostMedia) -> Unit,
) {
    com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMediaAttachment(
        modifier = modifier,
        medias = media.medias,
        mediaAltMaxCharacters = mediaAltMaxCharacters,
        onAltChanged = onAltChanged,
        onDeleteClick = onDeleteClick,
    )
}
