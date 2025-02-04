package com.zhangke.fread.status.ui.poll

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_poll_vote_finished_tip
import com.zhangke.fread.statusui.status_ui_poll_votes_finished_tip
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlogPoll(
    modifier: Modifier,
    poll: BlogPoll,
    isSelf: Boolean,
    blogTranslationState: BlogTranslationUiState,
    onVoted: (List<BlogPoll.Option>) -> Unit,
) {
    // 显示投票占比，满足任意条件：发帖人、投票结束、已投票
    Column(modifier = modifier) {
        if (poll.multiple) {
            MultipleChoicePoll(poll, isSelf, blogTranslationState, onVoted)
        } else {
            SingleChoicePoll(poll, isSelf, blogTranslationState, onVoted)
        }
        if (poll.expired) {
            val count = poll.votesCount
            val finishedTip = if (count <= 1) {
                stringResource(Res.string.status_ui_poll_vote_finished_tip, count)
            } else {
                stringResource(Res.string.status_ui_poll_votes_finished_tip, count)
            }
            Text(
                modifier = Modifier.padding(start = 4.dp, top = 8.dp),
                text = finishedTip,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
