package com.zhangke.fread.status.ui.label

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun StatusBottomTimeLabel(
    modifier: Modifier,
    blog: Blog,
    specificTime: String,
    style: StatusStyle,
    onUrlClick: (String) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = specificTime,
            style = style.bottomLabelStyle.textStyle,
            color = style.secondaryFontColor,
        )
        if (blog.application?.name.isNullOrEmpty().not()) {
            Text(
                modifier = Modifier.noRippleClick(
                    enabled = !blog.application?.website.isNullOrEmpty()
                ) {
                    onUrlClick(blog.application!!.website!!)
                },
                color = style.secondaryFontColor,
                text = "  •  ${blog.application!!.name}",
                style = style.bottomLabelStyle.textStyle,
            )
        }
    }
}
