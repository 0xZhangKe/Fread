package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.freadPlaceholder
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
    PreviewCardContainer(
        modifier = modifier,
        onRemoveClick = onRemoveClick,
        link = link,
    ) {

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
    Box(
        modifier = modifier.border(
            width = 1.dp,
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.outlineVariant,
        )
    ) {
        Text(
            modifier = Modifier.align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(start = 32.dp, top = 16.dp, end = 32.dp),
            text = link,
            textAlign = TextAlign.Center,
            maxLines = 1,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            overflow = TextOverflow.Ellipsis,
        )
        IconButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onRemoveClick,
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(LocalizedString.statusUiDelete),
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 16.dp, top = 46.dp, end = 16.dp, bottom = 16.dp),
        ) {
            content()
        }
    }
}

sealed interface DetectedLinkCard {

    val link: String

    data class Loading(override val link: String) : DetectedLinkCard

    data class Loaded(override val link: String, val info: LinkPreviewInfo) : DetectedLinkCard

    data class Failure(override val link: String, val throwable: Throwable?) : DetectedLinkCard

    data class Deleted(override val link: String) : DetectedLinkCard
}
