package com.zhangke.utopia.pages.feeds.shared.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

data class StatusSourceUiState(
    val uri: String,
    val name: String,
    val description: String,
    val thumbnail: String?,
    val addEnabled: Boolean,
    val removeEnabled: Boolean,
)

class StatusSourceUiStateAdapter @Inject constructor() {

    fun adapt(
        source: StatusSource,
        addEnabled: Boolean,
        removeEnabled: Boolean,
    ): StatusSourceUiState {
        return StatusSourceUiState(
            uri = source.uri,
            name = source.name,
            description = source.description,
            thumbnail = source.thumbnail,
            addEnabled = addEnabled,
            removeEnabled = removeEnabled,
        )
    }
}

@Composable
fun StatusSource(
    modifier: Modifier = Modifier,
    source: StatusSourceUiState,
    onAddClick: (() -> Unit)? = null,
    onRemoveClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier,
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (avatar, centerLine, title, desc, options) = createRefs()
            Box(
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .constrainAs(centerLine) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
            )
            if (source.thumbnail.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(5.dp)
                        .height(45.dp)
                        .constrainAs(avatar) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                )
            } else {
                AsyncImage(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .size(45.dp)
                        .clip(CircleShape)
                        .constrainAs(avatar) {
                            start.linkTo(parent.start, 15.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    model = source.thumbnail,
                    contentDescription = "Avatar",
                )
            }
            Text(
                modifier = Modifier.constrainAs(title) {
                    bottom.linkTo(parent.bottom, 3.dp)
                    start.linkTo(parent.start, 10.dp)
                    end.linkTo(options.start, 6.dp)
                },
                maxLines = 1,
                text = source.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                modifier = Modifier
                    .padding(bottom = 15.dp)
                    .constrainAs(desc) {
                        top.linkTo(parent.top, 3.dp)
                        start.linkTo(title.start)
                        end.linkTo(options.start, 8.dp)
                    },
                text = source.description,
                fontSize = 14.sp,
            )
            Row(
                modifier = Modifier.constrainAs(options) {
                    end.linkTo(parent.end, 6.dp)
                    top.linkTo(parent.top)
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
                            painter = rememberVectorPainter(image = Icons.Default.Add),
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
                            painter = rememberVectorPainter(image = Icons.Default.Remove),
                            contentDescription = "Remove",
                        )
                    }
                }
            }
        }
    }
}
