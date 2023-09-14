package com.zhangke.utopia.status.ui.poll

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.status.blog.BlogPoll

@Composable
fun BlogPoll(
    modifier: Modifier,
    poll: BlogPoll,
) {

    poll.options.forEach {
        BlogPollOption(
            modifier = Modifier.fillMaxWidth(),
            option = it,
            ratio = 0.5F,
        )
    }
}

@Composable
private fun BlogPollOption(
    modifier: Modifier,
    option: BlogPoll.Option,
    ratio: Float,
) {
    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .padding(top = 10.dp, bottom = 10.dp)
    ) {

        Text(
            modifier = Modifier
                .padding(start = 45.dp, end = 15.dp)
                .align(Alignment.CenterStart)
                .fillMaxWidth(),
            text = option.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
