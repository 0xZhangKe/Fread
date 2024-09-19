package com.zhangke.fread.feeds.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.feeds.R
import com.zhangke.fread.status.ui.utils.CardInfoSection
import com.zhangke.fread.status.uri.FormalUri

data class StatusSourceUiState(
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
    val context = LocalContext.current
    CardInfoSection(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        avatar = source.thumbnail,
        title = source.name,
        description = source.description,
        onUrlClick = {
            BrowserLauncher.launchWebTabInApp(context, it)
        },
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
                var showDeleteConfirmDialog by remember {
                    mutableStateOf(false)
                }
                IconButton(
                    modifier = Modifier.size(24.dp),
                    onClick = { showDeleteConfirmDialog = true },
                    enabled = onRemoveClick != null,
                ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Default.Delete),
                        contentDescription = "Remove",
                    )
                }
                if (showDeleteConfirmDialog) {
                    FreadDialog(
                        onDismissRequest = { showDeleteConfirmDialog = false },
                        contentText = stringResource(id = R.string.feeds_delete_confirm_content),
                        onNegativeClick = {
                            showDeleteConfirmDialog = false
                        },
                        onPositiveClick = {
                            showDeleteConfirmDialog = false
                            onRemoveClick?.invoke()
                        },
                    )
                }
            }
        }
    )
}
