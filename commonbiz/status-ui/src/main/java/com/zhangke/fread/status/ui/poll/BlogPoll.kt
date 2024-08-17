package com.zhangke.fread.status.ui.poll

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.statusui.R

@Composable
fun BlogPoll(
    modifier: Modifier,
    poll: BlogPoll,
    blogTranslationState: BlogTranslationUiState,
    onVoted: (List<BlogPoll.Option>) -> Unit,
) {
    Column(modifier = modifier) {
        if (poll.multiple) {
            MultipleChoicePoll(poll, blogTranslationState, onVoted)
        } else {
            SingleChoicePoll(poll, blogTranslationState, onVoted)
        }
        if (poll.expired) {
            val count = poll.votesCount
            val finishedTip = if (count <= 1) {
                stringResource(R.string.status_ui_poll_vote_finished_tip, count)
            } else {
                stringResource(R.string.status_ui_poll_votes_finished_tip, count)
            }
            Text(
                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                text = finishedTip,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
