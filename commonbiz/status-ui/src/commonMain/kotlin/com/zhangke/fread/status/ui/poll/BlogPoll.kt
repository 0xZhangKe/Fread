package com.zhangke.fread.status.ui.poll

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.BlogTranslationUiState
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlogPoll(
    modifier: Modifier,
    poll: BlogPoll,
    isSelf: Boolean?,
    blogTranslationState: BlogTranslationUiState,
    onVoted: (List<BlogPoll.Option>) -> Unit,
) {
    // 显示投票占比，满足任意条件：发帖人、投票结束、已投票
    Column(modifier = modifier) {
        if (poll.multiple) {
            MultipleChoicePoll(poll, isSelf == true, blogTranslationState, onVoted)
        } else {
            SingleChoicePoll(poll, isSelf == true, blogTranslationState, onVoted)
        }
        if (poll.expired) {
            val count = poll.votesCount
            val finishedTip = if (count <= 1) {
                stringResource(LocalizedString.statusUiPollVoteFinishedTip, count)
            } else {
                stringResource(LocalizedString.statusUiPollVotesFinishedTip, count)
            }
            Text(
                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                text = finishedTip,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
