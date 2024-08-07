package com.zhangke.fread.status.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.horizontalPadding
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.R

@Composable
fun ReblogTopLabel(
    author: BlogAuthor,
    style: StatusStyle,
    onAuthorClick: (BlogAuthor) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = style.containerTopPadding)
            .padding(start = style.containerStartPadding, end = style.containerEndPadding)
            .noRippleClick { onAuthorClick(author) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            imageVector = ImageVector.vectorResource(R.drawable.ic_status_forward),
            contentDescription = null,
        )
        FreadRichText(
            modifier = Modifier.padding(start = 6.dp),
            richText = author.humanizedName,
            maxLines = 1,
            onHashtagClick = {},
            onMentionClick = {},
            onUrlClick = {},
            fontSizeSp = style.contentSize.topLabelSize.value,
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = stringResource(R.string.status_ui_forward),
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            fontSize = style.contentSize.topLabelSize,
        )
    }
}
