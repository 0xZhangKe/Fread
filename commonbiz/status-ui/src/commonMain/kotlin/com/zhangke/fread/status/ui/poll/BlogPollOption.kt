package com.zhangke.fread.status.ui.poll

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.SlickRoundCornerShape
import com.zhangke.framework.utils.pxToDp
import kotlin.math.roundToInt

@Composable
internal fun BlogPollOption(
    modifier: Modifier,
    selected: Boolean,
    votable: Boolean,
    showProgress: Boolean,
    optionContent: String,
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
    onClick: () -> Unit,
) {
    val density = LocalDensity.current
    val colorScheme = MaterialTheme.colorScheme
    var containerSize: IntSize? by remember {
        mutableStateOf(null)
    }
    val borderWidth = 1.dp
    val cornerRadius = 21.dp
    Box(
        modifier = modifier
            .onSizeChanged {
                if (it != containerSize) {
                    containerSize = it
                }
            }
            .heightIn(min = 42.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(cornerRadius),
            )
            .clickable(votable) { onClick() },
    ) {
        val fixedContainerSize = containerSize
        if (showProgress && fixedContainerSize != null && progress > 0F) {
            val progressWidth = fixedContainerSize.width * progress.coerceAtMost(1F)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = borderWidth)
                    .size(
                        height = fixedContainerSize.height.pxToDp(density),
                        width = progressWidth.pxToDp(density),
                    )
                    .clip(SlickRoundCornerShape(cornerRadius))
                    .background(color = Color.Blue.copy(alpha = 0.3F)),
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 18.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
                .align(Alignment.CenterStart)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.widthIn(max = 250.dp),
                text = optionContent,
                color = colorScheme.primary,
            )
            if (selected) {
                Icon(
                    modifier = Modifier
                        .padding(start = 6.dp)
                        .size(14.dp),
                    painter = rememberVectorPainter(Icons.Default.Check),
                    contentDescription = "",
                )
            }
        }
        if (showProgress) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp),
                text = "${(progress * 100).roundToInt()} %",
                color = Color.Black,
            )
        }
    }
}
