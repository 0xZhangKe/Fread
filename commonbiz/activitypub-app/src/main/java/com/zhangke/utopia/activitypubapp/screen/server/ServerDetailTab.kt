package com.zhangke.utopia.activitypubapp.screen.server

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.utopia.activitypubapp.R
import com.zhangke.utopia.activitypubapp.model.ActivityPubInstanceRule
import com.zhangke.utopia.activitypubapp.screen.server.about.ServerAboutPage
import com.zhangke.utopia.activitypubapp.screen.server.trending.tags.ServerTrendsTagsPage

internal enum class ServerDetailTab(
    val title: TextString,
    val content: @Composable Screen.(
        host: String,
        rules: List<ActivityPubInstanceRule>,
        contentCanScrollBackward: MutableState<Boolean>,
    ) -> Unit,
) {

    ABOUT(
        title = textOf(R.string.activity_pub_about),
        content = @Composable { host, rules, contentCanScrollBackward ->
            ServerAboutPage(host, rules, contentCanScrollBackward)
        },
    ),

    TRENDS_TAG(
        title = textOf(R.string.activity_pub_trends_tag),
        content = @Composable { host, _, contentCanScrollBackward ->
            ServerTrendsTagsPage(host, contentCanScrollBackward)
        },
    ),

    PLACEHOLDER(
        title = textOf("PlaceHolder"),
        content = @Composable { _, _, contentCanScrollBackward ->
            val listState = rememberLazyListState()
            val canScrollBackward by remember {
                derivedStateOf {
                    listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
                }
            }
            contentCanScrollBackward.value = canScrollBackward
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                state = listState,
            ) {
                items(100) {
                    Text(text = "item $it", modifier = Modifier.padding(4.dp))
                }
            }
        },
    ),
}
