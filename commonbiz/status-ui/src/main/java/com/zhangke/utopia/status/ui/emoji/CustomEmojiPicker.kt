package com.zhangke.utopia.status.ui.emoji

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.utopia.status.emoji.CustomEmoji

@Composable
fun CustomEmojiPicker(
    modifier: Modifier,
    emojiList: List<CustomEmojiCell>,
    onEmojiPick: (CustomEmoji) -> Unit,
) {
    Box(modifier = modifier) {
        LazyColumn {
            items(emojiList) { cell ->
                when (cell) {
                    is CustomEmojiCell.Title -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.CenterStart),
                                text = cell.title
                            )
                        }
                    }

                    is CustomEmojiCell.EmojiLine -> {
                        EmojiLineUi(
                            emojiLine = cell,
                            onEmojiClick = onEmojiPick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmojiLineUi(
    emojiLine: CustomEmojiCell.EmojiLine,
    onEmojiClick: (CustomEmoji) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(modifier = Modifier.weight(1F))
        emojiLine.emojiList.forEachIndexed { _, emoji ->
            AsyncImage(
                modifier = Modifier
                    .size(20.dp)
                    .noRippleClick { onEmojiClick(emoji) },
                model = emoji.url,
                contentDescription = emoji.shortcode,
            )
            Box(modifier = Modifier.weight(1F))
        }
    }
}

sealed interface CustomEmojiCell {

    data class Title(val title: String) : CustomEmojiCell

    data class EmojiLine(val emojiList: List<CustomEmoji>) : CustomEmojiCell
}
