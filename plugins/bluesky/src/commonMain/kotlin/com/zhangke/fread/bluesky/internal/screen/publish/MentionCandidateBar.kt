package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.bsky.actor.ProfileView
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.fread.status.ui.BlogAuthorAvatar

@Composable
fun MentionCandidateBar(
    uiState: PublishPostUiState,
    onMentionClick: (ProfileView) -> Unit,
) {
    val mentionState = uiState.mentionState
    if (mentionState.isIdle || mentionState.isFailed) return
    Spacer(modifier = Modifier.height(8.dp))
    if (mentionState.isLoading) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
            items(10) {
                MentionedItem(null, onMentionClick)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    } else if (mentionState.isSuccess) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 6.dp)
        ) {
            items(mentionState.requireSuccessData()) {
                MentionedItem(it, onMentionClick)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun MentionedItem(
    account: ProfileView?,
    onMentionClick: (ProfileView) -> Unit,
) {
    Row(
        modifier = Modifier
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(12),
                color = MaterialTheme.colorScheme.outline,
            )
            .clickable(account != null) { account?.let(onMentionClick) }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.width(6.dp))
        BlogAuthorAvatar(
            modifier = Modifier.size(18.dp),
            imageUrl = account?.avatar?.uri,
        )
        Text(
            modifier = Modifier
                .padding(start = 4.dp)
                .widthIn(min = 80.dp)
                .freadPlaceholder(account?.handle.isNullOrEmpty()),
            text = account?.handle?.handle.orEmpty(),
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.width(6.dp))
    }
}
