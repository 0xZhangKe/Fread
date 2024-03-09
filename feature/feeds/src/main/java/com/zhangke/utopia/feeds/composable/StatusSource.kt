package com.zhangke.utopia.feeds.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.status.ui.utils.CardInfoSection
import com.zhangke.utopia.status.uri.FormalUri

internal data class StatusSourceUiState(
    val uri: FormalUri,
    val name: String,
    val description: String,
    val thumbnail: String?,
    val addEnabled: Boolean,
    val removeEnabled: Boolean,
)

@Composable
internal fun RemovableStatusSource(
    modifier: Modifier = Modifier,
    source: StatusSourceUiState,
    onRemoveClick: () -> Unit,
) {
    StatusSourceNode(
        modifier = modifier,
        source = source,
        onRemoveClick = onRemoveClick,
    )
}

@Composable
internal fun StatusSourceNode(
    modifier: Modifier = Modifier,
    source: StatusSourceUiState,
    onAddClick: (() -> Unit)? = null,
    onRemoveClick: (() -> Unit)? = null,
) {
    CardInfoSection(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        avatar = source.thumbnail,
        title = source.name,
        description = source.description,
        actions = {
            if (source.addEnabled) {
                IconButton(
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp),
                    onClick = { onAddClick?.invoke() },
                    enabled = onAddClick != null,
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Rounded.AddCircleOutline),
                        contentDescription = "Add",
                    )
                }
            }
            if (source.removeEnabled) {
                IconButton(
                    modifier = Modifier.size(24.dp),
                    onClick = { onRemoveClick?.invoke() },
                    enabled = onRemoveClick != null,
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Default.Delete),
                        contentDescription = "Remove",
                    )
                }
            }
        }
    )
}
