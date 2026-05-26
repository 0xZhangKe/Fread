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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.zhangke.framework.composable.rememberTransientModalBottomSheetState
import com.zhangke.framework.utils.transparentIndicatorColors
import com.zhangke.fread.common.alttext.AltTextGenerator
import com.zhangke.fread.commonbiz.shared.screen.publish.alt.AltGeneratorButton
import com.zhangke.fread.commonbiz.shared.screen.publish.alt.AltGeneratorState
import com.zhangke.fread.commonbiz.shared.screen.publish.alt.GenerateState
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.ui.common.RemainingTextStatus
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

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
        AutoSizeImage(
            url = image.uri,
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
                text = stringResource(LocalizedString.sharedAltLabel),
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
            imageUri = image.uri,
            isVideo = isVideo,
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
    isVideo: Boolean,
    onDismissRequest: () -> Unit,
    alt: String,
    maxCharacters: Int,
    onAltChanged: (String) -> Unit,
) {
    val sheetState = rememberTransientModalBottomSheetState(skipPartiallyExpanded = true)
    val altTextGenerator: AltTextGenerator = koinInject()
    val coroutineScope = rememberCoroutineScope()
    val generatorButtonState = remember(altTextGenerator, coroutineScope) {
        AltGeneratorState(altTextGenerator, coroutineScope)
    }
    val generateState by generatorButtonState.generateState
    val snackbarHostState = remember { SnackbarHostState() }

    val altTextAvailable by generatorButtonState.available

    var inputtedValue by remember(alt) { mutableStateOf(alt) }

    LaunchedEffect(generateState) {
        when (val state = generateState) {

            is GenerateState.Success -> {
                inputtedValue = state.alt
            }

            is GenerateState.Failure -> {
                snackbarHostState.showSnackbar(state.errorMessage)
            }

            else -> {}
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(end = 16.dp, bottom = 24.dp),
            ) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = stringResource(LocalizedString.sharedPublishMediaAltDialogTitle),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                )

                Card(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
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
                        if (generateState is GenerateState.Generating) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.4F)),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                    }
                }

                if (altTextAvailable && !isVideo) {
                    AltGeneratorButton(
                        modifier = Modifier.padding(start = 8.dp),
                        state = generatorButtonState,
                        imageUri = imageUri,
                    )
                }

                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = stringResource(LocalizedString.sharedPublishMediaAltDialogInputTip),
                    style = MaterialTheme.typography.labelMedium,
                )

                TextField(
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp),
                        ),
                    value = inputtedValue,
                    onValueChange = { inputtedValue = it },
                    minLines = 1,
                    placeholder = { Text(text = stringResource(LocalizedString.sharedPublishMediaAltDialogInputHint)) },
                    colors = TextFieldDefaults.transparentIndicatorColors.copy(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RemainingTextStatus(
                        modifier = Modifier.padding(start = 16.dp),
                        maxCount = maxCharacters,
                        contentLength = inputtedValue.length,
                    )
                    Button(
                        modifier = Modifier.padding(start = 16.dp).weight(1F),
                        enabled = generateState !is GenerateState.Generating,
                        onClick = {
                            onAltChanged(inputtedValue)
                            onDismissRequest()
                        },
                    ) {
                        Text(text = stringResource(LocalizedString.save))
                    }
                }
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

private fun formatCost(cost: Double): String {
    if (cost == 0.0) return "$0"
    val digits = when {
        cost >= 0.01 -> 4
        cost >= 0.001 -> 5
        else -> 6
    }
    val rounded = ((cost * pow10(digits)) + if (cost < 0) -0.5 else 0.5).toLong()
    val str = (rounded.toDouble() / pow10(digits)).toString()
    return "$$str"
}

private fun pow10(n: Int): Double {
    var result = 1.0
    repeat(n) { result *= 10.0 }
    return result
}

interface PublishPostMedia {

    val uri: String

    val alt: String?

    val isVideo: Boolean
}
