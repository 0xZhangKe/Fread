package com.zhangke.fread.status.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zhangke.framework.blurhash.blurhash
import com.zhangke.fread.status.blog.PreviewCard
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun StatusPreviewCardUi(
    modifier: Modifier,
    card: PreviewCard,
    style: StatusStyle,
    onCardClick: (PreviewCard) -> Unit,
) {
    val containerModifier = modifier
        .border(
            width = 1.5.dp,
            color = DividerDefaults.color,
            shape = RoundedCornerShape(8.dp),
        )
        .clickable { onCardClick(card) }
        .padding(bottom = style.cardStyle.contentVerticalPadding)
    if (card.image.isNullOrEmpty().not()) {
        Column(modifier = containerModifier) {
            if (card.image.isNullOrEmpty().not()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(card.aspectRatio)
                ) {
                    AsyncImage(
                        modifier = Modifier
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .fillMaxSize()
                            .let {
                                if (card.blurhash.isNullOrEmpty()) {
                                    it
                                } else {
                                    it.blurhash(card.blurhash!!)
                                }
                            },
                        model = card.image,
                        contentDescription = "Preview Image",
                    )
                    if (card.type == PreviewCard.CardType.VIDEO) {
                        IconButton(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.Center),
                            onClick = {
                                onCardClick(card)
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play Video",
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(style.cardStyle.imageBottomPadding))
            PreviewCardTexts(card, style, 2)
        }
    } else {
        Row(
            modifier = containerModifier
                .height(86.dp)
                .padding(top = style.cardStyle.contentVerticalPadding),
        ) {
            Column(
                modifier = Modifier
                    .weight(1F)
                    .padding(end = 8.dp)
            ) {
                PreviewCardTexts(card, style, 1)
            }
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
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
        }
    }
}

@Composable
private fun PreviewCardTexts(
    card: PreviewCard,
    style: StatusStyle,
    maxLine: Int,
) {
    if (card.providerName.isNotEmpty()) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = card.providerName,
            style = style.cardStyle.descStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        textAlign = TextAlign.Start,
        text = card.title,
        style = style.cardStyle.titleStyle,
        maxLines = maxLine,
        overflow = TextOverflow.Ellipsis,
    )
    if (card.description.isNotEmpty()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Start,
            text = card.description,
            style = style.cardStyle.descStyle,
            maxLines = maxLine,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
