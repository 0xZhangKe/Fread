package com.zhangke.utopia.activitypub.app.internal.screen.instance

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstanceRule
import com.zhangke.utopia.activitypub.app.internal.screen.instance.about.ServerAboutPage
import com.zhangke.utopia.activitypub.app.internal.screen.instance.trending.ServerTrendingPage
import com.zhangke.utopia.activitypub.app.internal.screen.instance.trending.tags.ServerTrendsTagsPage

internal enum class ServerDetailTab(
    val title: TextString,
    val content: @Composable Screen.(
        baseUrl: FormalBaseUrl,
        rules: List<ActivityPubInstanceRule>,
        contentCanScrollBackward: MutableState<Boolean>,
    ) -> Unit,
) {

    ABOUT(
        title = textOf(R.string.activity_pub_about),
        content = @Composable { baseUrl, rules, contentCanScrollBackward ->
            ServerAboutPage(baseUrl, rules, contentCanScrollBackward)
        },
    ),

    TRENDS(
        title = textOf(R.string.activity_pub_trends_status),
        content = @Composable { baseUrl, _, contentCanScrollBackward ->
            ServerTrendingPage(baseUrl, contentCanScrollBackward)
        },
    ),

    TRENDS_TAG(
        title = textOf(R.string.activity_pub_trends_tag),
        content = @Composable { baseUrl, _, contentCanScrollBackward ->
            ServerTrendsTagsPage(baseUrl, contentCanScrollBackward)
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
