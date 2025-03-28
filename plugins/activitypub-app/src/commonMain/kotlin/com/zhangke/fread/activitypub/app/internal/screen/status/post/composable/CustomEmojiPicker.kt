package com.zhangke.fread.activitypub.app.internal.screen.status.post.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.FreadTabRow
import com.zhangke.framework.composable.icons.Tofu
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.activitypub.app.internal.model.CustomEmoji
import kotlinx.coroutines.launch

@Composable
fun CustomEmojiPicker(
    modifier: Modifier,
    emojiList: List<GroupedCustomEmojiCell>,
    onEmojiPick: (CustomEmoji) -> Unit,
) {
    if (emojiList.isEmpty()) return
    val coroutineScope = rememberCoroutineScope()
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val pagerState = rememberPagerState {
                emojiList.size
            }

            var selectedTabIndex by remember {
                mutableIntStateOf(0)
            }

            LaunchedEffect(pagerState.currentPage) {
                selectedTabIndex = pagerState.currentPage
            }

            FreadTabRow(
                selectedTabIndex = selectedTabIndex,
                tabCount = emojiList.size,
                tabContent = {
                    Text(text = emojiList[it].title)
                },
                onTabClick = {
                    selectedTabIndex = it
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(it)
                    }
                },
            )

            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth(),
                state = pagerState,
            ) { pageIndex ->
                CustomEmojiPickerPage(
                    emojis = emojiList[pageIndex].emojiList,
                    onEmojiClick = onEmojiPick,
                )
            }
        }
    }
}

@Composable
private fun CustomEmojiPickerPage(
    emojis: List<CustomEmoji>,
    onEmojiClick: (CustomEmoji) -> Unit,
) {
    if (emojis.isEmpty()) return
    LazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        columns = GridCells.Fixed(7),
    ) {
        items(emojis) { emoji ->
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .noRippleClick { onEmojiClick(emoji) },
                contentAlignment = Alignment.Center,
            ) {
                val placePainter = rememberVectorPainter(Icons.Default.Tofu)
                AutoSizeImage(
                    emoji.url,
                    modifier = Modifier.size(26.dp),
                    errorPainter = { placePainter },
                    placeholderPainter = { placePainter },
                    contentDescription = emoji.shortcode,
                )
            }
        }
    }
}

data class GroupedCustomEmojiCell(
    val title: String,
    val emojiList: List<CustomEmoji>,
)
