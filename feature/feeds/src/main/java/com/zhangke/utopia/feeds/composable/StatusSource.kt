package com.zhangke.utopia.feeds.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage

internal data class StatusSourceUiState(
    val uri: String,
    val name: String,
    val description: String,
    val thumbnail: String?,
    val addEnabled: Boolean,
    val removeEnabled: Boolean,
)


@Composable
internal fun StatusSourceSection(
    modifier: Modifier = Modifier,
    source: StatusSourceUiState,
) {
    StatusSourceNode(
        modifier = modifier,
        source = source,
    )
}

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
internal fun AddableStatusSource(
    modifier: Modifier = Modifier,
    source: StatusSourceUiState,
    onAddClick: () -> Unit,
) {
    StatusSourceNode(
        modifier = modifier,
        source = source,
        onAddClick = onAddClick,
    )
}

@Composable
internal fun StatusSourceNode(
    modifier: Modifier = Modifier,
    source: StatusSourceUiState,
    onAddClick: (() -> Unit)? = null,
    onRemoveClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, bottom = 20.dp),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (avatar, content, options) = createRefs()
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .constrainAs(avatar) {
                        start.linkTo(parent.start, 16.dp)
                        end.linkTo(content.start)
                        top.linkTo(parent.top, 12.dp)
                    },
                placeholder = painterResource(id = com.zhangke.utopia.commonbiz.R.drawable.ic_avatar),
                error = painterResource(id = com.zhangke.utopia.commonbiz.R.drawable.ic_avatar),
                model = source.thumbnail,
                contentDescription = "Avatar",
            )
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(content) {
                        start.linkTo(avatar.end, 16.dp)
                        top.linkTo(parent.top, 12.dp)
                        end.linkTo(options.start)
                        bottom.linkTo(parent.bottom, 12.dp)
                        width = Dimension.fillToConstraints
                    },
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    maxLines = 1,
                    text = source.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (source.description.isNotBlank()) {
                    Text(
                        modifier = Modifier.padding(top = 2.dp),
                        maxLines = 3,
                        text = source.description,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Row(
                modifier = Modifier.constrainAs(options) {
                    start.linkTo(content.end, 16.dp)
                    end.linkTo(parent.end, 24.dp)
                    top.linkTo(parent.top, 12.dp)
                    bottom.linkTo(parent.bottom)
                }
            ) {
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
        }
    }
}
