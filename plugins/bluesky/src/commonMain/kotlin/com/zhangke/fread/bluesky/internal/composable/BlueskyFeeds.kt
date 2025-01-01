package com.zhangke.fread.bluesky.internal.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImageActionPainter
import com.seiko.imageloader.ui.AutoSizeBox
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.fread.bluesky.Res
import com.zhangke.fread.bluesky.bsky_feeds_explorer_creator_label
import com.zhangke.fread.bluesky.bsky_feeds_explorer_liked_by
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlueskyFollowingFeeds(
    modifier: Modifier,
    feeds: BlueskyFeeds,
    onFeedsClick: (BlueskyFeeds) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeedsAvatar(feeds.avatar.orEmpty(), Modifier)

        Text(
            modifier = Modifier.weight(1F).padding(horizontal = 16.dp),
            text = feeds.displayName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            modifier = Modifier,
            contentDescription = null,
        )
    }
}

@Composable
fun BlueskyExploringFeeds(
    modifier: Modifier,
    feeds: BlueskyFeeds,
    onFeedsClick: (BlueskyFeeds) -> Unit,
    onAddClick: ((BlueskyFeeds) -> Unit)? = null,
) {
    Column(
        modifier = modifier.clickable { onFeedsClick(feeds) },
    ) {
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FeedsAvatar(feeds.avatar.orEmpty(), Modifier)
            Column(
                modifier = Modifier.weight(1F).padding(horizontal = 16.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = feeds.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                    text = stringResource(
                        Res.string.bsky_feeds_explorer_creator_label,
                        feeds.creator.displayName.orEmpty()
                    ),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7F),
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (onAddClick != null) {
                IconButton(onClick = { onAddClick(feeds) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Feeds",
                    )
                }
            }
        }
        if (feeds.description != null) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                text = feeds.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 8,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            text = stringResource(
                Res.string.bsky_feeds_explorer_liked_by,
                (feeds.likeCount ?: 0L).toString(),
            ),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7F),
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@Composable
private fun FeedsAvatar(
    url: String,
    modifier: Modifier,
) {
    AutoSizeBox(
        modifier = modifier,
        request = remember(url) {
            ImageRequest(url)
        },
    ) { action ->
        Image(
            painter = rememberImageActionPainter(action),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(6.dp))
                .freadPlaceholder(action !is ImageAction.Success),
        )
    }
}
