package com.zhangke.fread.bluesky.internal.screen.publish

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
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.Grid
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.utils.transparentColors
import com.zhangke.fread.commonbiz.shared.screen.Res
import com.zhangke.fread.commonbiz.shared.screen.shared_alt_label
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_media_alt_dialog_input_hint
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_media_alt_dialog_input_tip
import com.zhangke.fread.commonbiz.shared.screen.shared_publish_media_alt_dialog_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun PublishPostMediaAttachment(
    modifier: Modifier,
    media: PublishPostMediaAttachment,
) {
    when (media) {
        is PublishPostMediaAttachment.Image -> {
            val images = media.files
            if (images.isEmpty()) return
            Grid(
                modifier = modifier,
                columnCount = 2,
            ) {
                images.forEach { image ->
                    PublishPostMediaAttachmentImage(
                        modifier = Modifier.fillMaxWidth().aspectRatio(1F),
                        image = image,
                    )
                }
            }
        }

        is PublishPostMediaAttachment.Video -> {

        }
    }
}

@Composable
private fun PublishPostMediaAttachmentImage(
    modifier: Modifier,
    image: PublishPostMediaAttachmentFile,
    onAltChanged: (String) -> Unit,
    onDeleteClick: (PublishPostMediaAttachmentFile) -> Unit,
) {
    val shadowColor = Color.Black.copy(alpha = 0.7F)
    val fontColor = Color.White
    var showAltDialog by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        AutoSizeImage(
            url = image.uri,
            modifier = Modifier.fillMaxSize(),
            contentDescription = image.alt,
        )
        Row(
            modifier = Modifier.padding(start = 6.dp, top = 6.dp)
                .background(
                    color = shadowColor,
                    shape = RoundedCornerShape(6.dp),
                ).noRippleClick { showAltDialog = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(6.dp))
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
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(Res.string.shared_alt_label),
                color = fontColor,
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Box(
            modifier = Modifier.noRippleClick { onDeleteClick(image) }
                .padding(top = 6.dp, end = 6.dp)
                .background(
                    color = shadowColor,
                    shape = CircleShape,
                ),
        ) {
            Icon(
                modifier = Modifier.size(14.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = fontColor,
            )
        }
    }
    if (showAltDialog) {
        PublishPostImageAltDialog(
            onDismissRequest = { showAltDialog = false },
            alt = image.alt.orEmpty(),
            onAltChanged = onAltChanged,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PublishPostImageAltDialog(
    imageUri: String,
    onDismissRequest: () -> Unit,
    alt: String,
    onAltChanged: (String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
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
                modifier = Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp),
                ),
                value = inputtedValue,
                onValueChange = { inputtedValue = it },
                minLines = 3,
                placeholder = { Text(stringResource(Res.string.shared_publish_media_alt_dialog_input_hint)) },
                colors = TextFieldDefaults.transparentColors,
            )
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {

            }
        }
    }
}
