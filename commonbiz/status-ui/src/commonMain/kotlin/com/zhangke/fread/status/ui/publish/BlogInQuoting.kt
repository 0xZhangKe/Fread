package com.zhangke.fread.status.ui.publish

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.ui.action.quoteIcon
import com.zhangke.fread.status.ui.embed.BlogInEmbedding
import com.zhangke.fread.status.ui.embed.embedBorder
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun BlogInQuoting(
    modifier: Modifier,
    blog: Blog,
    style: StatusStyle,
) {
    Column(
        modifier = modifier,
    ) {
        Icon(
            modifier = Modifier.rotate(180F),
            imageVector = quoteIcon(),
            tint = MaterialTheme.colorScheme.outline,
            contentDescription = null,
        )
        Spacer(Modifier.height(8.dp))
        BlogInEmbedding(
            modifier = Modifier
                .embedBorder()
                .padding(8.dp),
            blog = blog,
            style = style,
        )
    }
}
