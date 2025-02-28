package com.zhangke.fread.status.ui.publish

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.TwoTextsInRow

@Composable
fun NameAndAccountInfo(
    modifier: Modifier,
    name: String,
    handle: String,
    style: PublishBlogStyle,
) {
    TwoTextsInRow(
        firstText = {
            Text(
                modifier = Modifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = name,
                style = style.nameStyle,
                textAlign = TextAlign.Start,
            )
        },
        secondText = {
            Text(
                modifier = Modifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = handle,
                style = style.handleStyle,
            )
        },
        spacing = 2.dp,
        modifier = modifier,
    )
}
