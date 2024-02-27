package com.zhangke.utopia.status.ui.poll

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.utopia.status.blog.BlogPoll

@Composable
fun BlogPoll(
    modifier: Modifier,
    poll: BlogPoll,
    onVoted: (List<BlogPoll.Option>) -> Unit,
) {
    if (poll.multiple) {
        MultipleChoicePoll(modifier = modifier, poll = poll, onVoted = onVoted)
    } else {
        SingleChoicePoll(
            modifier = modifier,
            poll = poll,
            onVoted = onVoted,
        )
    }
}
