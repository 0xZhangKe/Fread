package com.zhangke.fread.status.ui.hashtag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zhangke.framework.composable.BezierCurve
import com.zhangke.framework.composable.BezierCurveStyle
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.textString
import com.zhangke.framework.utils.toPx
import com.zhangke.fread.status.model.Hashtag

@Composable
fun HashtagUi(
    tag: Hashtag,
    onClick: (Hashtag) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp)
            .clickable {
                onClick(tag)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp)
        ) {
            Text(
                text = tag.name,
                fontSize = 16.sp,
            )
            Text(
                modifier = Modifier.padding(top = 3.dp),
                text = textString(tag.description),
                fontSize = 12.sp,
            )
        }

        BezierCurve(
            modifier = Modifier
                .padding(end = 15.dp)
                .size(width = 70.dp, height = 40.dp),
            points = tag.history.history.reversed(),
            minPoint = tag.history.min,
            maxPoint = tag.history.max,
            style = BezierCurveStyle.StrokeAndFill(
                fillBrush = SolidColor(MaterialTheme.colorScheme.primary),
                strokeBrush = SolidColor(Color.White),
                stroke = Stroke(width = 1.dp.toPx()),
            )
        )
    }
}

@Composable
fun HashtagUiPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(width = 100.dp, height = 16.dp)
                    .freadPlaceholder(true)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(width = 200.dp, height = 16.dp)
                    .freadPlaceholder(true)
            )
        }

        BezierCurve(
            modifier = Modifier
                .padding(end = 15.dp)
                .size(width = 70.dp, height = 40.dp),
            points = listOf(0.4F, 0.5F, 0.4F, 0.5F, 0.6F, 0.7F),
            minPoint = 0.3F,
            maxPoint = 0.7F,
            style = BezierCurveStyle.StrokeAndFill(
                fillBrush = SolidColor(DividerDefaults.color),
                strokeBrush = SolidColor(DividerDefaults.color),
                stroke = Stroke(width = 1.dp.toPx()),
            )
        )
    }
}
