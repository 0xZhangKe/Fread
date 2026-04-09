package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.TextWithIcon
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.utils.LinkPreviewInfo
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource

@Composable
fun LinkPreviewCard(
    modifier: Modifier,
    card: DetectedLinkCard,
    onRemoveClick: () -> Unit,
) {
    when (card) {
        is DetectedLinkCard.Deleted -> {
            Box(modifier = modifier)
        }

        is DetectedLinkCard.Loading -> {
            PreviewCardLoading(
                modifier = modifier,
                link = card.link,
                onRemoveClick = onRemoveClick,
            )
        }

        is DetectedLinkCard.Loaded -> {
            PreviewCardLoaded(
                modifier = modifier,
                link = card.link,
                info = card.info,
                onRemoveClick = onRemoveClick,
            )
        }

        is DetectedLinkCard.Failure -> {
            PreviewCardFailure(
                modifier = modifier,
                link = card.link,
                throwable = card.throwable,
                onRemoveClick = onRemoveClick,
            )
        }
    }
}

@Composable
private fun PreviewCardLoading(modifier: Modifier, link: String, onRemoveClick: () -> Unit) {
    PreviewCardContainer(
        modifier = modifier,
        onRemoveClick = onRemoveClick,
        link = link,
    ) {
        Box(
            modifier = Modifier.height(16.dp)
                .fillMaxWidth(0.3F)
                .freadPlaceholder(true),
        )
        Box(
            modifier = Modifier.padding(top = 16.dp)
                .height(16.dp)
                .fillMaxWidth(0.6F)
                .freadPlaceholder(true),
        )
        Box(
            modifier = Modifier.padding(top = 16.dp)
                .height(16.dp)
                .fillMaxWidth(0.9F)
                .freadPlaceholder(true),
        )
    }
}

@Composable
private fun PreviewCardLoaded(
    modifier: Modifier,
    link: String,
    info: LinkPreviewInfo,
    onRemoveClick: () -> Unit,
) {
    if (info.image.isNullOrEmpty()) {
        Row(
            modifier = modifier.height(86.dp).previewCardBorder(),
        ) {
            Box(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxHeight()
                    .aspectRatio(1F)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceDim,
                        shape = RoundedCornerShape(8.dp),
                    ),
            ) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp),
                    imageVector = Icons.Default.Description,
                    contentDescription = "Link",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7F),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1F)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Start,
                    text = info.title,
                    maxLines = 2,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp)
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Start,
                    text = info.description.ifNullOrEmpty { info.siteName.ifNullOrEmpty { link } },
                    maxLines = 3,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            DeleteButton(
                modifier = Modifier.padding(top = 8.dp, end = 8.dp),
                onClick = onRemoveClick,
            )
        }
    } else {
        Column(
            modifier = modifier.previewCardBorder()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(2F),
            ) {
                AutoSizeImage(
                    remember(info.image) {
                        ImageRequest(info.image.orEmpty())
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Preview Image",
                )
                DeleteButton(
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp),
                    onClick = onRemoveClick,
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(horizontal = 16.dp),
                textAlign = TextAlign.Start,
                text = info.title,
                maxLines = 2,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                overflow = TextOverflow.Ellipsis,
            )
            if (!info.description.isNullOrEmpty()) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Start,
                    text = info.description.orEmpty(),
                    maxLines = 3,
                    fontSize = 14.sp,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 12.dp, end = 16.dp)
            )
            TextWithIcon(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                text = info.siteName.ifNullOrEmpty { info.url },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PreviewCardFailure(
    modifier: Modifier,
    link: String,
    throwable: Throwable?,
    onRemoveClick: () -> Unit,
) {
    PreviewCardContainer(
        modifier = modifier,
        onRemoveClick = onRemoveClick,
        link = link,
    ) {
        Text(
            modifier = Modifier.padding(top = 16.dp).align(Alignment.CenterHorizontally),
            text = stringResource(LocalizedString.loadMoreError),
            fontWeight = FontWeight.SemiBold,
        )
        if (!throwable?.message.isNullOrEmpty()) {
            Text(
                modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally),
                text = throwable.message.orEmpty(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PreviewCardContainer(
    modifier: Modifier,
    link: String,
    onRemoveClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = modifier.previewCardBorder()) {
        Text(
            modifier = Modifier.align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = 46.dp, top = 16.dp, end = 46.dp),
            text = link,
            textAlign = TextAlign.Center,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
        )
        DeleteButton(
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 8.dp, end = 8.dp),
            onClick = onRemoveClick,
        )
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 16.dp, top = 46.dp, end = 16.dp, bottom = 16.dp),
        ) {
            content()
        }
    }
}

@Composable
private fun DeleteButton(
    modifier: Modifier,
    onClick: () -> Unit,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ),
) {
    IconButton(
        modifier = modifier,
        onClick = onClick,
        colors = colors,
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(LocalizedString.statusUiDelete),
        )
    }
}

@Composable
private fun Modifier.previewCardBorder(): Modifier {
    return this.border(
        width = 1.dp,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

sealed interface DetectedLinkCard {

    val link: String

    data class Loading(override val link: String) : DetectedLinkCard

    data class Loaded(override val link: String, val info: LinkPreviewInfo) : DetectedLinkCard

    data class Failure(override val link: String, val throwable: Throwable?) : DetectedLinkCard

    data class Deleted(override val link: String) : DetectedLinkCard
}
