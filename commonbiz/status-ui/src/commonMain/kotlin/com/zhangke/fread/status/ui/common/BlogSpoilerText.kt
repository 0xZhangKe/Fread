package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.zhangke.framework.architect.theme.inverseOnSurfaceDark
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.utils.toPx
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.richtext.FreadRichText

@Composable
fun SpoilerText(
    modifier: Modifier,
    hideContent: Boolean,
    spoilerText: RichText,
    fontSize: TextUnit,
    onShowContent: () -> Unit,
    onHideContent: () -> Unit,
    onUrlClick: (url: String) -> Unit,
    onHashtagInStatusClick: (HashtagInStatus) -> Unit,
    onMentionClick: (Mention) -> Unit,
    onMentionDidClick: (String) -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .drawSpoilerBackground()
            .noRippleClick {
                if (hideContent) {
                    onShowContent()
                } else {
                    onHideContent()
                }
            }
    ) {
        FreadRichText(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(start = 16.dp, top = 22.dp, end = 16.dp, bottom = 6.dp),
            richText = spoilerText,
            color = inverseOnSurfaceDark,
            onMentionClick = onMentionClick,
            onMentionDidClick = onMentionDidClick,
            onHashtagClick = onHashtagInStatusClick,
            onUrlClick = onUrlClick,
            fontSize = fontSize,
        )
    }
}

@Composable
fun Modifier.drawSpoilerBackground(): Modifier {
    val edgeColor = Color(0xFFFFB84D)
    val backgroundColor = Color(0xFFFFEED3)
    val edgeWidth = 8.dp.toPx()
    val cornerRadiiPx = 6.dp.toPx()
    val cornerRadius = CornerRadius(cornerRadiiPx, cornerRadiiPx)
    return this.drawBehind {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val startEdge = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = Offset.Zero,
                        Size(width = edgeWidth, height = canvasHeight),
                    ),
                    topLeft = cornerRadius,
                    bottomLeft = cornerRadius,
                )
            )
        }
        val endEdge = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(
                        offset = Offset(x = canvasWidth - edgeWidth, y = 0F),
                        Size(width = edgeWidth, height = canvasHeight),
                    ),
                    topRight = cornerRadius,
                    bottomRight = cornerRadius,
                )
            )
        }
        drawPath(startEdge, edgeColor)
        drawPath(endEdge, edgeColor)
        drawRect(
            color = backgroundColor,
            topLeft = Offset(x = edgeWidth, y = 0F),
            size = size.copy(width = canvasWidth - edgeWidth * 2),
        )
        var pointStartOffset = 18.dp.toPx()
        val pointRadii = 1.5.dp.toPx()
        repeat(3) {
            drawCircle(
                color = Color.Black.copy(alpha = 0.8F),
                radius = pointRadii,
                center = Offset(x = pointStartOffset, y = 14.dp.toPx())
            )
            pointStartOffset += pointRadii + 6.dp.toPx()
        }
    }
}
