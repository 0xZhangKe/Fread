package com.zhangke.utopia.status.ui.poll

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.ui.image.BlogMediaClickEvent
import com.zhangke.utopia.status.ui.image.OnBlogMediaClick

@Composable
internal fun SingleChoicePoll(
    modifier: Modifier,
    poll: BlogPoll,
    votedOption: (List<BlogPoll.Option>) -> Unit,
) {
    Column(modifier = modifier) {
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
                    votedOption(listOf(option))
                },
            )
            if (index < poll.options.lastIndex) {
                Spacer(modifier = Modifier.size(width = 1.dp, height = 10.dp))
            }
        }
    }

}
