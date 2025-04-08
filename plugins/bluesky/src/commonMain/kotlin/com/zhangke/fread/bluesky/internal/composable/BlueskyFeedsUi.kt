package com.zhangke.fread.bluesky.internal.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImageActionPainter
import com.seiko.imageloader.ui.AutoSizeBox
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.utils.formatToHumanReadable
import com.zhangke.fread.bluesky.Res
import com.zhangke.fread.bluesky.bsky_feeds_explorer_creator_label
import com.zhangke.fread.bluesky.bsky_feeds_explorer_liked_by
import com.zhangke.fread.bluesky.bsky_feeds_item_subtitle
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.statusui.ic_drag_indicator
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlueskyFollowingFeeds(
    modifier: Modifier,
    feeds: BlueskyFeeds,
    onFeedsClick: (BlueskyFeeds) -> Unit,
) {
    Row(
        modifier = modifier.clickable { onFeedsClick(feeds) }
            .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeedsAvatar(feeds.avatar, Modifier)
        Column(
            modifier = Modifier.weight(1F).padding(horizontal = 16.dp),
        ) {
            Text(
                text = feeds.displayName(),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
            )
            val subtitle = buildFeedsSubtitle(feeds)
            if (!subtitle.isNullOrEmpty()) {
                Text(
                    modifier = Modifier.padding(top = 1.dp),
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Icon(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp)
                .alpha(0.7F)
                .padding(2.dp),
            painter = painterResource(com.zhangke.fread.statusui.Res.drawable.ic_drag_indicator),
            contentDescription = "Drag for reorder Content Config",
        )
    }
}

@Composable
private fun buildFeedsSubtitle(feeds: BlueskyFeeds): String? {
    if (feeds !is BlueskyFeeds.Feeds) return null
    return stringResource(
        Res.string.bsky_feeds_item_subtitle,
        feeds.creator.prettyHandle,
        (feeds.likeCount ?: 0).formatToHumanReadable(),
    )
}

@Composable
fun BlueskyExploringFeeds(
    modifier: Modifier,
    feeds: BlueskyFeeds,
    loading: Boolean = false,
    onFeedsClick: (BlueskyFeeds) -> Unit,
    onAddClick: ((BlueskyFeeds) -> Unit)? = null,
) {
    Column(
        modifier = modifier.clickable { onFeedsClick(feeds) }.padding(horizontal = 16.dp),
    ) {
        Spacer(modifier = Modifier.size(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FeedsAvatar(feeds.avatar, Modifier)
            Column(
                modifier = Modifier.weight(1F).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = feeds.displayName(),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis,
                )

                if (!feeds.creatorName.isNullOrEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                        text = stringResource(
                            Res.string.bsky_feeds_explorer_creator_label,
                            feeds.creatorName!!,
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F),
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (!feeds.pinned && onAddClick != null && !loading) {
                IconButton(onClick = { onAddClick(feeds) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Feeds",
                    )
                }
            } else if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(18.dp))
            }
        }
        if (feeds.description != null) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                text = feeds.description!!,
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
                (feeds.likeCount ?: 0L).formatToHumanReadable(),
            ),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Start,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.size(8.dp))
    }
}

@Composable
internal fun FeedsAvatar(
    url: String?,
    modifier: Modifier,
) {
    if (url.isNullOrEmpty()) {
        Icon(
            modifier = modifier
                .size(42.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.primary),
            imageVector = Icons.AutoMirrored.Filled.ListAlt,
            tint = Color.White,
            contentDescription = "Avatar",
        )
    } else {
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
}

private val BlueskyFeeds.avatar: String?
    get() {
        return when (this) {
            is BlueskyFeeds.Feeds -> avatar
            is BlueskyFeeds.List -> avatar
            else -> null
        }
    }

private val BlueskyFeeds.description: String?
    get() {
        return when (this) {
            is BlueskyFeeds.Feeds -> description
            is BlueskyFeeds.List -> description
            else -> null
        }
    }

private val BlueskyFeeds.likeCount: Long?
    get() {
        return when (this) {
            is BlueskyFeeds.Feeds -> likeCount
            else -> null
        }
    }

private val BlueskyFeeds.creatorName: String?
    get() {
        return when (this) {
            is BlueskyFeeds.Feeds -> creator.displayName
            else -> null
        }
    }
