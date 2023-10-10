package com.zhangke.utopia.status.ui.poll

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.status.blog.BlogPoll

@Composable
fun BlogPoll(
    modifier: Modifier,
    poll: BlogPoll,
    onVote: (List<BlogPoll.Option>) -> Unit,
) {
    if (poll.multiple) {
        MultipleChoicePoll(modifier = modifier, poll = poll, onVote = onVote)
    } else {
        SingleChoicePoll(
            modifier = modifier,
            poll = poll,
            onVote = {
                onVote(listOf(it))
            },
        )
    }
}
