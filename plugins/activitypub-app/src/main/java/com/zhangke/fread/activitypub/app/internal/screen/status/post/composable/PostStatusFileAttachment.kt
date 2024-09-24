package com.zhangke.fread.activitypub.app.internal.screen.status.post.composable

import android.graphics.Bitmap
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.utils.getThumbnail
import com.zhangke.framework.utils.prettyString
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.screen.status.post.InputMediaDescriptionScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusAttachment
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusMediaAttachmentFile
import com.zhangke.fread.activitypub.app.internal.screen.status.post.UploadMediaJob
import com.zhangke.fread.commonbiz.image
import com.zhangke.fread.commonbiz.video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val MEDIA_ASPECT = 1.78F

@Composable
internal fun PostStatusVideoAttachment(
    modifier: Modifier,
    attachment: PostStatusAttachment.Video,
    onDeleteClick: (PostStatusMediaAttachmentFile) -> Unit,
    onCancelUploadClick: (PostStatusMediaAttachmentFile.LocalFile) -> Unit,
    onRetryClick: (PostStatusMediaAttachmentFile.LocalFile) -> Unit,
    onDescriptionInputted: (PostStatusMediaAttachmentFile, String) -> Unit,
) {
    Box(
        modifier = modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
            .aspectRatio(MEDIA_ASPECT)
    ) {
        val attachmentFile = attachment.video
        MediaFileContent(
            modifier = Modifier.fillMaxSize(),
            file = attachmentFile,
            onDeleteClick = { onDeleteClick(attachmentFile) },
            onCancelUploadClick = {
                attachmentFile.let { it as? PostStatusMediaAttachmentFile.LocalFile }
                    ?.let(onCancelUploadClick)
            },
            onRetryClick = {
                attachmentFile.let { it as? PostStatusMediaAttachmentFile.LocalFile }
                    ?.let(onRetryClick)
            },
            onDescriptionInputted = { onDescriptionInputted(attachmentFile, it) },
        )
    }
}

@Composable
internal fun PostStatusImageAttachment(
    modifier: Modifier,
    attachment: PostStatusAttachment.Image,
    onDeleteClick: (PostStatusMediaAttachmentFile) -> Unit,
    onCancelUploadClick: (PostStatusMediaAttachmentFile.LocalFile) -> Unit,
    onRetryClick: (PostStatusMediaAttachmentFile.LocalFile) -> Unit,
    onDescriptionInputted: (PostStatusMediaAttachmentFile, String) -> Unit,
) {
    Box(
        modifier = modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        ImagesLayout(
            count = attachment.imageList.size,
            itemContent = { index ->
                val attachmentFile = attachment.imageList[index]
                MediaFileContent(
                    modifier = Modifier.fillMaxSize(),
                    file = attachmentFile,
                    onDeleteClick = { onDeleteClick(attachmentFile) },
                    onCancelUploadClick = {
                        attachmentFile.let { it as? PostStatusMediaAttachmentFile.LocalFile }
                            ?.let(onCancelUploadClick)
                    },
                    onRetryClick = {
                        attachmentFile.let { it as? PostStatusMediaAttachmentFile.LocalFile }
                            ?.let(onRetryClick)
                    },
                    onDescriptionInputted = { onDescriptionInputted(attachmentFile, it) },
                )
            }
        )
    }
}

@Composable
private fun MediaFileContent(
    modifier: Modifier,
    file: PostStatusMediaAttachmentFile,
    onDeleteClick: () -> Unit,
    onCancelUploadClick: () -> Unit,
    onRetryClick: () -> Unit,
    onDescriptionInputted: (String) -> Unit,
) {
    Card(
        modifier = modifier,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .fillMaxWidth()
                    .weight(1F)
            ) {
                if (file is PostStatusMediaAttachmentFile.LocalFile && file.isVideo) {
                    var bitmap: Bitmap? by remember(file) {
                        mutableStateOf(null)
                    }
                    LaunchedEffect(file) {
                        launch(Dispatchers.IO) {
                            bitmap = file.file.uri.getThumbnail()
                        }
                    }
                    if (bitmap != null) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            bitmap = bitmap!!.asImageBitmap(),
                            contentDescription = "preview",
                        )
                    } else {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            imageVector = Icons.Default.Error,
                            contentDescription = "preview",
                        )
                    }
                } else {
                    AutoSizeImage(
                        remember(file.previewUri) {
                            ImageRequest(file.previewUri)
                        },
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        contentDescription = "Media",
                    )
                }
            }

            Text(
                modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp),
                text = file.description.ifNullOrEmpty { stringResource(R.string.post_screen_media_descriptor_placeholder) },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (file is PostStatusMediaAttachmentFile.LocalFile) {
                val mediaType = if (file.file.isVideo) {
                    org.jetbrains.compose.resources.stringResource(com.zhangke.fread.commonbiz.Res.string.video)
                } else {
                    org.jetbrains.compose.resources.stringResource(com.zhangke.fread.commonbiz.Res.string.image)
                }
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 2.dp, end = 16.dp),
                    text = "${file.file.size.prettyString} / $mediaType",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Box(
                    modifier = Modifier
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
                                file = file,
                                onDescriptionInputted = onDescriptionInputted,
                                onDeleteClick = onDeleteClick,
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp)
                ) {
                    ImageAttachmentBottomSuccess(
                        file = file,
                        onDescriptionInputted = onDescriptionInputted,
                        onDeleteClick = onDeleteClick,
                    )
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
            progress = { progress },
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1F),
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
            progress = { 1F },
            modifier = Modifier
                .weight(1F)
                .align(Alignment.CenterVertically),
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
    file: PostStatusMediaAttachmentFile,
    onDescriptionInputted: (String) -> Unit,
    onDeleteClick: () -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    Row(
        modifier = Modifier
            .align(Alignment.CenterEnd)
    ) {
        SimpleIconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically),
            onClick = {
                navigator.push(
                    InputMediaDescriptionScreen(
                        previewUrl = file.previewUri,
                        description = file.description,
                        onDescriptionInputted = onDescriptionInputted,
                    )
                )
            },
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
