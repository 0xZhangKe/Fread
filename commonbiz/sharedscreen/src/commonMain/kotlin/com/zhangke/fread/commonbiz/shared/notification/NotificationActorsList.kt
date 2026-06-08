package com.zhangke.fread.commonbiz.shared.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.ui.BlogAuthorAvatar

@Composable
fun NotificationActorsList(
    modifier: Modifier,
    actors: List<BlogAuthor>,
    onActorClick: (BlogAuthor) -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        actors.forEach { author ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onActorClick(author) }
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BlogAuthorAvatar(
                    modifier = Modifier.size(28.dp),
                    imageUrl = author.avatar,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = author.name.ifEmpty { author.prettyHandle.removePrefix("@") },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    modifier = Modifier.weight(1F),
                    text = author.prettyHandle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
