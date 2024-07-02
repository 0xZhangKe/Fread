package com.zhangke.fread.screen

import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.loadable.lazycolumn.LoadMoreUi
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.screen.content.ActivityPubContentScreen
import com.zhangke.fread.activitypub.app.internal.screen.content.timeline.ActivityPubTimelineTab
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.R
import com.zhangke.fread.screen.main.MainPage
import com.zhangke.fread.status.model.Emoji
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.richtext.android.HtmlParser
import com.zhangke.fread.status.richtext.android.span.CustomEmojiSpan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class FreadScreen : BaseScreen() {

    @Composable
    private fun ScrollDemo() {
        val pagerState = rememberPagerState {
            2
        }
        val connection = remember(pagerState) {
            PagerDefaults.pageNestedScrollConnection(pagerState, Orientation.Horizontal)
        }
        HorizontalPager(
            modifier = Modifier,
            state = pagerState,
            pageNestedScrollConnection = connection,
        ) {
            if (it == 0) {
                val list = remember {
                    List(100) {
                        "Item $it"
                    }
                }
                LazyColumn(
                    modifier = Modifier.fillMaxSize().nestedScroll(connection),
                ) {
                    items(list) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            var expand by remember {
                                mutableStateOf(false)
                            }
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clickable {
                                        expand = !expand
                                    },
                                text = if (expand) "$it \n $it \n $it" else it,
                            )
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp),
                                painter = painterResource(id = R.drawable.illustration_celebrate),
                                contentDescription = "",
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(text = "aaa")
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        super.Content()
//        MainPage()

        ScrollDemo()
    }
}
