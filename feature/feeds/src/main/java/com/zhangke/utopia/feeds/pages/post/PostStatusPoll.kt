package com.zhangke.utopia.feeds.pages.post

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.zhangke.utopia.feeds.R

@Composable
internal fun PostStatusPoll(
    modifier: Modifier,
    poll: PostStatusAttachment.Poll,
    onPollContentChanged: (Int, String) -> Unit,
) {
    Column(modifier = modifier) {
        poll.optionList.forEachIndexed { index, option ->
            var inputtedText: String by remember {
                mutableStateOf(option)
            }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.outline,
                    )
                    .onFocusChanged {
                        onPollContentChanged(index, inputtedText)
                    },
                value = option,
                onValueChange = {
                    inputtedText = it
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.post_status_poll_item_hint, index),
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent,
                ),
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = stringResource(R.string.post_status_poll_duration),
                    style = MaterialTheme.typography.labelSmall,
                )
                val duration = poll.duration
                Text(
                    text = stringResource(R.string.post_status_poll_duration),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(DividerDefaults.color)
            )

        }
    }
}
