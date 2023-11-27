package com.zhangke.utopia.feeds.pages.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.utopia.feeds.R

private const val MEDIA_ASPECT = 1.78F

@Composable
internal fun PostStatusImageAttachment(
    modifier: Modifier,
    attachment: PostStatusAttachment.ImageAttachment,
    onDeleteClick: (PostStatusFile) -> Unit,
    onCancelUploadClick: (PostStatusFile) -> Unit,
    onRetryClick: (PostStatusFile) -> Unit,
    onDescriptionInputted: (PostStatusFile, String) -> Unit,
) {
    Box(
        modifier = modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        ImagesLayout(
            count = attachment.imageList.size,
            itemContent = { index ->
                val attachmentFile = attachment.imageList[index]
                ImageMediaContent(
                    modifier = Modifier.fillMaxSize(),
                    file = attachmentFile,
                    onDeleteClick = { onDeleteClick(attachmentFile) },
                    onCancelUploadClick = { onCancelUploadClick(attachmentFile) },
                    onRetryClick = { onRetryClick(attachmentFile) },
                    onDescriptionInputted = { onDescriptionInputted(attachmentFile, it) },
                )
            }
        )
    }
}

@Composable
private fun ImageMediaContent(
    modifier: Modifier,
    file: PostStatusFile,
    onDeleteClick: () -> Unit,
    onCancelUploadClick: () -> Unit,
    onRetryClick: () -> Unit,
    onDescriptionInputted: (String) -> Unit,
) {
    Card(
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .fillMaxWidth()
                    .weight(1F),
                model = file.uri,
                contentScale = ContentScale.Crop,
                contentDescription = "Media",
            )

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp),
                text = file.description.ifNullOrEmpty { stringResource(R.string.post_screen_media_placeholder) },
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val imageString = stringResource(R.string.image)
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 2.dp, end = 16.dp),
                text = "${file.size} / $imageString",
                style = MaterialTheme.typography.bodySmall,
            )

            Box(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                val uploadState by file.uploadJob.uploadState.collectAsState()
                when (uploadState) {
                    is UploadMediaJob.UploadState.Uploading -> {
                        ImageAttachmentBottomLoading(
                            uploadState = uploadState as UploadMediaJob.UploadState.Uploading,
                            onCancelUploadClick = onCancelUploadClick,
                        )
                    }

                    is UploadMediaJob.UploadState.Failed -> {
                        ImageAttachmentBottomFailed(
                            onRetryClick = onRetryClick,
                        )
                    }

                    else -> {
                        ImageAttachmentBottomSuccess(
                            onDescriptionInputted = onDescriptionInputted,
                            onDeleteClick = onDeleteClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.ImageAttachmentBottomLoading(
    uploadState: UploadMediaJob.UploadState.Uploading,
    onCancelUploadClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center)
    ) {
        val progress by uploadState.progress.collectAsState(initial = 0F)
        LinearProgressIndicator(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1F),
            progress = progress,
        )
        SimpleIconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = onCancelUploadClick,
            imageVector = Icons.Default.Close,
            contentDescription = "Stop",
        )
    }
}

@Composable
private fun BoxScope.ImageAttachmentBottomFailed(
    onRetryClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.Center)
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .weight(1F)
                .align(Alignment.CenterVertically),
            progress = 1F,
            color = MaterialTheme.colorScheme.error,
            trackColor = MaterialTheme.colorScheme.error,
        )
        SimpleIconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = onRetryClick,
            imageVector = Icons.Default.Refresh,
            contentDescription = "Retry",
        )
    }
}

@Composable
private fun BoxScope.ImageAttachmentBottomSuccess(
    onDescriptionInputted: (String) -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .align(Alignment.CenterEnd)
    ) {
        SimpleIconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            onClick = onDeleteClick,
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit",
        )
        SimpleIconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = onDeleteClick,
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
        )
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
