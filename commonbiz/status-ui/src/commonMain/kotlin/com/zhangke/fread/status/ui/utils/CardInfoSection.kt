package com.zhangke.fread.status.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.ui.BlogAuthorAvatar
import com.zhangke.fread.status.ui.richtext.FreadRichText

@Composable
fun CardInfoSection(
    modifier: Modifier,
    avatar: String?,
    title: String,
    handle: String,
    description: String?,
    logo: Painter? = null,
    onClick: () -> Unit,
    onUrlClick: (String) -> Unit,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    Card(
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            BlogAuthorAvatar(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                    .size(40.dp),
                imageUrl = avatar,
            )
            Column(
                modifier = Modifier
                    .weight(1F)
                    .padding(start = 8.dp, top = 16.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        maxLines = 1,
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (logo != null) {
                        Image(
                            modifier = Modifier.padding(start = 4.dp).size(16.dp),
                            painter = logo,
                            contentDescription = null,
                        )
                    }
                }
                Text(
                    maxLines = 1,
                    text = handle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
                if (description.isNullOrEmpty().not()) {
                    FreadRichText(
                        modifier = Modifier.padding(top = 2.dp),
                        content = description,
                        mentions = emptyList(),
                        emojis = emptyList(),
                        tags = emptyList(),
                        onHashtagClick = {},
                        onMentionClick = {},
                        onUrlClick = onUrlClick,
                        maxLines = 3,
                    )
                }
            }
            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                if (actions != null) {
                    Row(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        actions()
                    }
                }
            }
        }
    }
}
