package com.zhangke.utopia.feeds.pages.post

import android.net.Uri
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zhangke.framework.composable.SimpleIconButton

private const val MEDIA_ASPECT = 1.78F

@Composable
internal fun PostStatusImages(
    modifier: Modifier,
    uiState: PostStatusUiState,
    onDelete: (Uri) -> Unit,
) {
    Box(
        modifier = modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        ImagesLayout(
            count = uiState.mediaList.size,
            itemContent = { index ->
                val mediaUri = uiState.mediaList[index]
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(6.dp)),
                        model = mediaUri,
                        contentScale = ContentScale.Crop,
                        contentDescription = "Media",
                    )
                    SimpleIconButton(
                        modifier = Modifier
                            .align(Alignment.TopEnd),
                        onClick = { onDelete(mediaUri) },
                        imageVector = Icons.Default.Close,
                        tint = Color.White,
                        contentDescription = "Delete",
                    )
                }
            }
        )
    }
}

@Composable
private fun ImageMediaContent(
    modifier: Modifier,
) {
    Card(
        modifier = modifier,
    ) {

    }
}

@Composable
private fun ImagesLayout(
    count: Int,
    itemContent: @Composable (Int) -> Unit,
) {
    when (count) {
        1 -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(MEDIA_ASPECT)
            ) {
                itemContent(0)
            }
        }

        2 -> {
            DoubleMediaSingleRow(itemContent)
        }

        3, 4 -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                DoubleMediaSingleRow(itemContent = itemContent)
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(8.dp)
                )
                DoubleMediaSingleRow(
                    itemContent = {
                        val targetIndex = it + 2
                        if (targetIndex < count) {
                            itemContent(it + 2)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DoubleMediaSingleRow(
    itemContent: @Composable (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(MEDIA_ASPECT)
    ) {
        Box(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
        ) {
            itemContent(0)
        }
        Spacer(
            modifier = Modifier
                .height(1.dp)
                .width(8.dp)
        )
        Box(
            modifier = Modifier
                .weight(1F)
                .fillMaxHeight()
        ) {
            itemContent(1)
        }
    }
}