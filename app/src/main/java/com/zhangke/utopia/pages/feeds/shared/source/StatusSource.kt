package com.zhangke.utopia.pages.feeds.shared.source

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage

@Composable
fun StatusSourceNode(
    modifier: Modifier = Modifier,
    source: StatusSourceUiState,
    onAddClick: (() -> Unit)? = null,
    onRemoveClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier.padding(start = 15.dp, end = 15.dp, bottom = 12.dp),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val (avatar, content, options) = createRefs()
            if (source.thumbnail.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .height(45.dp)
                        .constrainAs(avatar) {
                            start.linkTo(parent.start)
                            end.linkTo(content.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                )
            } else {
                AsyncImage(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .constrainAs(avatar) {
                            start.linkTo(parent.start, 15.dp)
                            end.linkTo(content.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        },
                    model = source.thumbnail,
                    contentDescription = "Avatar",
                )
            }
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(content) {
                        start.linkTo(avatar.end, 10.dp)
                        top.linkTo(parent.top, 5.dp)
                        end.linkTo(options.start)
                        bottom.linkTo(parent.bottom, 8.dp)
                        width = Dimension.fillToConstraints
                    },
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    maxLines = 1,
                    text = source.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    modifier = Modifier
                        .padding(top = 2.dp),
                    text = source.description,
                    fontSize = 14.sp,
                )
            }
            Row(
                modifier = Modifier.constrainAs(options) {
                    start.linkTo(content.end)
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
