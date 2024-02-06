package com.zhangke.utopia.status.ui.hashtag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.zhangke.framework.composable.textString
import com.zhangke.framework.utils.toPx
import com.zhangke.utopia.status.model.Hashtag

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
                fillBrush = SolidColor(Color.Green),
                strokeBrush = SolidColor(Color.White),
                stroke = Stroke(width = 1.dp.toPx()),
            )
        )
    }
}
