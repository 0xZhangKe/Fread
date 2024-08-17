package com.zhangke.fread.status.ui.poll

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.status.blog.BlogPoll

@Composable
internal fun SingleChoicePoll(
    poll: BlogPoll,
    blogTranslationState: BlogTranslationUiState,
    onVoted: (List<BlogPoll.Option>) -> Unit,
) {
    val sum = poll.options.sumOf { it.votesCount ?: 0 }.toFloat()
    val translatedPoll = blogTranslationState.blogTranslation?.poll
    poll.options.forEachIndexed { index, option ->
        val votesCount = option.votesCount?.toFloat() ?: 0F
        val progress = if (votesCount > 0) votesCount / sum else 0F
        val selected = poll.ownVotes.contains(index)
        var optionContent: String = option.title
        if (blogTranslationState.showingTranslation) {
            translatedPoll?.options?.getOrNull(index)?.title?.let {
                optionContent = it
            }
        }
        BlogPollOption(
            modifier = Modifier.fillMaxWidth(),
            optionContent = optionContent,
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
