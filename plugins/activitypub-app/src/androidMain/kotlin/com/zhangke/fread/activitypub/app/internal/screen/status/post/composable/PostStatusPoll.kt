package com.zhangke.fread.activitypub.app.internal.screen.status.post.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.DurationSelector
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.utils.formattedString
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostBlogRules
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusAttachment
import com.zhangke.fread.activitypub.app.post_status_poll_duration
import com.zhangke.fread.activitypub.app.post_status_poll_function_title
import com.zhangke.fread.activitypub.app.post_status_poll_item_hint
import com.zhangke.fread.activitypub.app.post_status_poll_multiple
import com.zhangke.fread.activitypub.app.post_status_poll_single
import com.zhangke.fread.activitypub.app.post_status_poll_style_select_dialog_title
import kotlin.time.Duration

@Composable
internal fun PostStatusPoll(
    modifier: Modifier,
    poll: PostStatusAttachment.Poll,
    rules: PostBlogRules,
    onRemovePollClick: () -> Unit,
    onRemoveItemClick: (Int) -> Unit,
    onAddPollItemClick: () -> Unit,
    onPollContentChanged: (Int, String) -> Unit,
    onPollStyleSelect: (multiple: Boolean) -> Unit,
    onDurationSelect: (Duration) -> Unit,
) {
    Column(modifier = modifier.padding(start = 16.dp, end = 8.dp)) {
        poll.optionList.forEachIndexed { index, option ->
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    modifier = Modifier
                        .weight(1F)
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.outline,
                        )
                        .align(Alignment.CenterVertically),
                    value = option,
                    onValueChange = {
                        onPollContentChanged(index, it)
                    },
                    maxLines = 1,
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = stringResource(Res.string.post_status_poll_item_hint, index + 1),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                    ),
                )
                if (index == poll.optionList.lastIndex) {
                    if (poll.optionList.size < rules.maxPollOptions) {
                        SimpleIconButton(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            onClick = onAddPollItemClick,
                            imageVector = Icons.Rounded.Add,
                            contentDescription = "",
                        )
                    }
                } else {
                    SimpleIconButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        onClick = {
                            onRemoveItemClick(index)
                        },
                        imageVector = Icons.Rounded.Remove,
                        enabled = poll.optionList.size > 2,
                        contentDescription = "",
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )
        }
        Row(
            modifier = Modifier
                .padding(start = 8.dp)
                .fillMaxWidth()
                .height(38.dp)
        ) {
            var durationDialogVisible by remember {
                mutableStateOf(false)
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable {
                        durationDialogVisible = true
                    }
            ) {
                Text(
                    text = stringResource(Res.string.post_status_poll_duration),
                    style = MaterialTheme.typography.labelMedium,
                )
                Box(modifier = Modifier.weight(1F))
                val durationString by produceState("", poll.duration) {
                    value = poll.duration.formattedString()
                }
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = durationString,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (durationDialogVisible) {
                DurationSelector(
                    defaultDuration = poll.duration,
                    onDismissRequest = { durationDialogVisible = false },
                    onDurationSelect = onDurationSelect,
                )
            }
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, top = 6.dp, end = 16.dp, bottom = 4.dp)
                    .width(1.dp)
                    .fillMaxHeight()
                    .background(DividerDefaults.color)
            )
            var showChooseStyleDialog by remember {
                mutableStateOf(false)
            }
            Column(modifier = Modifier
                .fillMaxHeight()
                .clickable { showChooseStyleDialog = true }) {
                Text(
                    text = stringResource(Res.string.post_status_poll_function_title),
                    style = MaterialTheme.typography.labelSmall,
                )
                Box(modifier = Modifier.weight(1F))
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = if (poll.multiple) {
                        stringResource(Res.string.post_status_poll_multiple)
                    } else {
                        stringResource(Res.string.post_status_poll_single)
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (showChooseStyleDialog) {
                ChoosePollStyleDialog(
                    defaultMultiple = poll.multiple,
                    onDismissRequest = { showChooseStyleDialog = false },
                    onSelect = onPollStyleSelect,
                )
            }

            Box(
                modifier = Modifier
                    .height(1.dp)
                    .weight(1F)
            )

            SimpleIconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = onRemovePollClick,
                imageVector = Icons.Filled.Delete,
                contentDescription = "Remove poll",
            )
        }
    }
}

@Composable
private fun ChoosePollStyleDialog(
    defaultMultiple: Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (multiple: Boolean) -> Unit,
) {
    var multiple by remember(defaultMultiple) {
        mutableStateOf(defaultMultiple)
    }
    FreadDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(Res.string.post_status_poll_style_select_dialog_title),
        onNegativeClick = onDismissRequest,
        onPositiveClick = {
            onDismissRequest()
            onSelect(multiple)
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(start = 80.dp, top = 16.dp, end = 80.dp, bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = stringResource(Res.string.post_status_poll_single)
                    )
                    Box(modifier = Modifier.weight(1F))
                    RadioButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        selected = !multiple,
                        onClick = { multiple = false },
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        text = stringResource(Res.string.post_status_poll_multiple)
                    )
                    Box(modifier = Modifier.weight(1F))
                    RadioButton(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        selected = multiple,
                        onClick = { multiple = true },
                    )
                }
            }
        }
    )
}
