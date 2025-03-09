package com.zhangke.fread.commonbiz.shared.screen.publish

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.Grid
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.utils.transparentColors
import com.zhangke.fread.commonbiz.save
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_alt_label
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_media_alt_dialog_input_hint
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_media_alt_dialog_input_tip
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_media_alt_dialog_title
import com.zhangke.fread.status.ui.common.RemainingTextStatus
import org.jetbrains.compose.resources.stringResource

@Composable
fun PublishPostMediaAttachment(
    modifier: Modifier,
    medias: List<PublishPostMedia>,
    mediaAltMaxCharacters: Int,
    onAltChanged: (PublishPostMedia, String) -> Unit,
    onDeleteClick: (PublishPostMedia) -> Unit,
) {
    Grid(
        modifier = modifier,
        columnCount = 2,
        verticalSpacing = 16.dp,
        horizontalSpacing = 16.dp,
    ) {
        medias.forEach { media ->
            PublishPostMediaAttachmentImage(
                modifier = Modifier.fillMaxWidth().aspectRatio(1F),
                image = media,
                isVideo = media.isVideo,
                mediaAltMaxCharacters = mediaAltMaxCharacters,
                onAltChanged = onAltChanged,
                onDeleteClick = onDeleteClick,
            )
        }
    }
}

@Composable
private fun PublishPostMediaAttachmentImage(
    modifier: Modifier,
    image: PublishPostMedia,
    isVideo: Boolean,
    mediaAltMaxCharacters: Int,
    onAltChanged: (PublishPostMedia, String) -> Unit,
    onDeleteClick: (PublishPostMedia) -> Unit,
) {
    val shadowColor = Color.Black.copy(alpha = 0.7F)
    val fontColor = Color.White
    var showAltDialog by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        HorizontalDivider()
        AutoSizeImage(
            url = image.uri.toString(),
            modifier = Modifier.fillMaxSize()
                .clip(RoundedCornerShape(6.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(6.dp),
                ),
            contentDescription = image.alt,
            contentScale = ContentScale.Crop,
        )
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                .background(
                    color = shadowColor,
                    shape = RoundedCornerShape(2.dp),
                ).padding(start = 2.dp, top = 2.dp, bottom = 2.dp, end = 2.dp)
                .noRippleClick { showAltDialog = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val iconVector = if (image.alt.isNullOrEmpty()) {
                Icons.Default.Add
            } else {
                Icons.Default.Check
            }
            Icon(
                modifier = Modifier.size(14.dp),
                imageVector = iconVector,
                contentDescription = if (image.alt.isNullOrEmpty()) {
                    "Add ALT"
                } else {
                    "ALT Added"
                },
                tint = fontColor,
            )
            Spacer(modifier = Modifier.width(1.dp))
            Text(
                text = stringResource(Res.string.shared_alt_label),
                color = fontColor,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Box(
            modifier = Modifier.noRippleClick { onDeleteClick(image) }
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)
                .background(
                    color = shadowColor,
                    shape = CircleShape,
                ).padding(4.dp),
        ) {
            Icon(
                modifier = Modifier.size(14.dp),
                imageVector = Icons.Default.Close,
                contentDescription = "Delete",
                tint = fontColor,
            )
        }
        if (isVideo) {
            Box(
                modifier = Modifier.align(Alignment.Center)
                    .background(
                        color = shadowColor,
                        shape = CircleShape,
                    ).padding(1.dp),
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = fontColor,
                )
            }
        }
    }
    if (showAltDialog) {
        PublishPostImageAltDialog(
            imageUri = image.uri.toString(),
            onDismissRequest = { showAltDialog = false },
            alt = image.alt.orEmpty(),
            maxCharacters = mediaAltMaxCharacters,
            onAltChanged = { onAltChanged(image, it) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PublishPostImageAltDialog(
    imageUri: String,
    onDismissRequest: () -> Unit,
    alt: String,
    maxCharacters: Int,
    onAltChanged: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
        ) {
            Text(
                text = stringResource(Res.string.shared_publish_media_alt_dialog_title),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            )

            Card(
                modifier = Modifier.padding(top = 6.dp)
                    .fillMaxWidth()
                    .aspectRatio(1.7F),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    AutoSizeImage(
                        url = imageUri,
                        modifier = Modifier.align(Alignment.Center),
                        contentDescription = null,
                    )
                }
            }

            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(Res.string.shared_publish_media_alt_dialog_input_tip),
                style = MaterialTheme.typography.labelMedium,
            )

            var inputtedValue by remember(alt) { mutableStateOf(alt) }
            TextField(
                modifier = Modifier.padding(top = 8.dp)
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp),
                    ),
                value = inputtedValue,
                onValueChange = { inputtedValue = it },
                minLines = 1,
                placeholder = { Text(stringResource(Res.string.shared_publish_media_alt_dialog_input_hint)) },
                colors = TextFieldDefaults.transparentColors.copy(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RemainingTextStatus(
                    modifier = Modifier.padding(start = 16.dp),
                    maxCount = maxCharacters,
                    contentLength = inputtedValue.length,
                )
                Button(
                    modifier = Modifier.padding(start = 16.dp).weight(1F),
                    onClick = {
                        onAltChanged(inputtedValue)
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(com.zhangke.fread.commonbiz.Res.string.save))
                }
            }
        }
    }
}

interface PublishPostMedia {

    val uri: String

    val alt: String?

    val isVideo: Boolean
}
