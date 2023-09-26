package com.zhangke.utopia.status.ui.poll

import androidx.annotation.FloatRange
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.SlickRoundCornerShape
import com.zhangke.framework.utils.dpToPx
import com.zhangke.framework.utils.pxToDp
import com.zhangke.utopia.status.blog.BlogPoll

@Composable
fun BlogPoll(
    modifier: Modifier,
    poll: BlogPoll,
) {
    Column(modifier = modifier) {
        val sum = poll.options.sumOf { it.votesCount ?: 0 }.toFloat()
        poll.options.forEachIndexed { index, option ->
            val votesCount = option.votesCount?.toFloat() ?: 0F
            val progress = if (votesCount > 0) {
                votesCount / sum
            } else {
                0F
            }
            BlogPollOption(
                modifier = Modifier.fillMaxWidth(),
                optionContent = option.title,
                progress = progress,
            )
            if (index < poll.options.lastIndex) {
                Spacer(modifier = Modifier.size(width = 1.dp, height = 10.dp))
            }
        }
    }
}

@Composable
private fun BlogPollOption(
    modifier: Modifier,
    optionContent: String,
    @FloatRange(from = 0.0, to = 1.0) progress: Float,
) {
    val density = LocalDensity.current
    val colorScheme = MaterialTheme.colorScheme
    var containerSize: IntSize? by remember {
        mutableStateOf(null)
    }
    val borderWidth = 1.dp
    val cornerRadius = 21.dp
    val cornerRadiusPx = cornerRadius.dpToPx(density)
    Box(
        modifier = modifier
            .onSizeChanged {
                if (it != containerSize) {
                    containerSize = it
                }
            }
            .heightIn(min = 42.dp)
            .border(
                width = borderWidth,
                color = Color.Black,
                shape = RoundedCornerShape(cornerRadius),
            ),
    ) {
        val fixedContainerSize = containerSize
        if (fixedContainerSize != null && progress > 0F) {
            val progressWidth = fixedContainerSize.width * progress.coerceAtMost(1F)
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = borderWidth)
                    .clip(SlickRoundCornerShape(cornerRadius))
                    .background(color = Color.Blue.copy(alpha = 0.3F))
                    .size(
                        height = fixedContainerSize.height.pxToDp(density),
                        width = progressWidth.pxToDp(density),
                    ),
            )
//            Canvas(
//                modifier = Modifier
//                    .align(Alignment.CenterStart)
//                    .padding(start = borderWidth)
//                    .clipToBounds()
//                    .size(
//                        height = fixedContainerSize.height.pxToDp(density),
//                        width = progressWidth.pxToDp(density),
//                    ),
//                onDraw = {
//                    val nodeWidthDp = size.width.pxToDp(density)
//                    if (nodeWidthDp > cornerRadius) {
//                        drawRoundRect(
//                            color = Color.Blue.copy(alpha = 0.3F),
//                            size = size,
//                            cornerRadius = CornerRadius(cornerRadiusPx),
//                        )
//                    } else {
//                        drawCircle(
//                            color = Color.Blue.copy(alpha = 0.3F),
//                            radius = cornerRadiusPx,
//                            center = Offset(x = cornerRadiusPx, y = size.height / 2F),
//                        )
//                    }
//                }
//            )
        }
        Text(
            modifier = Modifier
                .padding(start = 18.dp, end = 15.dp, top = 10.dp, bottom = 10.dp)
                .align(Alignment.CenterStart)
                .fillMaxWidth(),
            text = optionContent,
            color = colorScheme.primary,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(backgroundColor = 0xffffffff)
@Composable
private fun PreviewMoreLineBlogPollOption() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(horizontal = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        BlogPollOption(
            modifier = Modifier
                .fillMaxWidth(),
            optionContent = "12344",
            progress = 0F,
        )
        BlogPollOption(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            optionContent = "12344",
            progress = 0.5F,
        )
        BlogPollOption(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            optionContent = "12344",
            progress = 1F,
        )
        BlogPollOption(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            optionContent = "123441234412344123441234412344123441234412344123441234412344123441234412344123441234412344123441234412344123441234412344123441234412344",
            progress = 0.5F,
        )
    }
}
