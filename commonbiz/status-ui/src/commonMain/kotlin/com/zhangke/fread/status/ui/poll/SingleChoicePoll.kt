package com.zhangke.fread.status.ui.poll

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.translate.PostTranslationState
import com.zhangke.fread.common.translate.PostTranslationStatus
import com.zhangke.fread.status.blog.BlogPoll

@Composable
internal fun SingleChoicePoll(
    poll: BlogPoll,
    isSelf: Boolean,
    postTranslationState: PostTranslationState,
    onVoted: (List<BlogPoll.Option>) -> Unit,
) {
    val sum = poll.options.sumOf { it.votesCount ?: 0 }.toFloat()
    val showTranslation =
        postTranslationState.showTranslation && (postTranslationState.status is PostTranslationStatus.Translated)
    val translatedPoll =
        (postTranslationState.status as? PostTranslationStatus.Translated)?.translatedContent?.poll
    val pollIsInVotable = !poll.expired && poll.voted == false && !isSelf
    poll.options.forEachIndexed { index, option ->
        val votesCount = option.votesCount?.toFloat() ?: 0F
        val progress = if (votesCount > 0) votesCount / sum else 0F
        val selected = poll.ownVotes.contains(index)
        val showProgress = isSelf || poll.expired || selected
        var optionContent: String = option.title
        if (showTranslation) {
            translatedPoll?.getOrNull(index)?.let { optionContent = it }
        }
        BlogPollOption(
            modifier = Modifier.fillMaxWidth(),
            optionContent = optionContent,
            selected = selected,
            votable = pollIsInVotable,
            showProgress = showProgress,
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
