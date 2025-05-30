package com.zhangke.fread.status.ui.poll

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.SlickRoundCornerShape
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_poll_vote
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MultipleChoicePoll(
    poll: BlogPoll,
    isSelf: Boolean,
    blogTranslationState: BlogTranslationUiState,
    onVoted: (List<BlogPoll.Option>) -> Unit,
) {
    val indexToSelected = remember(poll) {
        val map = mutableMapOf<Int, Boolean>()
        poll.options.forEachIndexed { index, _ ->
            map[index] = poll.ownVotes.contains(index)
        }
        mutableStateMapOf(*map.map { it.key to it.value }.toTypedArray())
    }
    val translatedPoll = blogTranslationState.blogTranslation?.poll
    val pollIsInVotable = !poll.expired && poll.voted == false && !isSelf
    val sum = poll.options.sumOf { it.votesCount ?: 0 }.toFloat()
    poll.options.forEachIndexed { index, option ->
        val votesCount = option.votesCount?.toFloat() ?: 0F
        val progress = if (votesCount > 0) votesCount / sum else 0F
        var optionContent: String = option.title
        if (blogTranslationState.showingTranslation) {
            translatedPoll?.options?.getOrNull(index)?.title?.let {
                optionContent = it
            }
        }
        val selected = indexToSelected[index] ?: false
        BlogPollOption(
            modifier = Modifier.fillMaxWidth(),
            optionContent = optionContent,
            selected = selected,
            votable = pollIsInVotable,
            showProgress = isSelf || selected || poll.expired,
            progress = progress,
            onClick = {
                indexToSelected[index] = indexToSelected[index]?.not() ?: false
            },
        )
        if (index < poll.options.lastIndex) {
            Spacer(modifier = Modifier.size(width = 1.dp, height = 10.dp))
        }
    }
    if (pollIsInVotable) {
        val votable = indexToSelected.map { it.value }.contains(true)
        val backgroundColor = if (votable) {
            Color.Blue.copy(alpha = 0.6F)
        } else {
            Color.Gray.copy(alpha = 0.6F)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
                .height(42.dp)
                .clip(SlickRoundCornerShape(21.dp))
                .background(backgroundColor)
                .clickable(votable) {
                    val votedOptions = indexToSelected
                        .filter { it.value }
                        .map { it.key }
                        .map { poll.options[it] }
                    onVoted(votedOptions)
                },
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(Res.string.status_ui_poll_vote),
            )
        }
    }
}
