package com.zhangke.fread.status.ui.poll

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.blog.BlogPoll

@Composable
internal fun ColumnScope.SingleChoicePoll(
    poll: BlogPoll,
    onVoted: (List<BlogPoll.Option>) -> Unit,
) {
    val sum = poll.options.sumOf { it.votesCount ?: 0 }.toFloat()
    poll.options.forEachIndexed { index, option ->
        val votesCount = option.votesCount?.toFloat() ?: 0F
        val progress = if (votesCount > 0) votesCount / sum else 0F
        val selected = poll.ownVotes.contains(index)
        BlogPollOption(
            modifier = Modifier.fillMaxWidth(),
            optionContent = option.title,
            selected = selected,
            votable = !poll.expired && poll.voted == false,
            showProgress = selected,
            progress = progress,
            onClick = {
                onVoted(listOf(option))
            },
        )
        if (index < poll.options.lastIndex) {
            Spacer(modifier = Modifier.size(width = 1.dp, height = 10.dp))
        }
    }
}
