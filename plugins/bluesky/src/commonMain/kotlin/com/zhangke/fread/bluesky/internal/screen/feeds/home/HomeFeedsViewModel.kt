package com.zhangke.fread.bluesky.internal.screen.feeds.home

import app.bsky.feed.GetFeedQueryParams
import app.bsky.graph.GetListQueryParams
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.launch
import sh.christian.ozone.api.AtUri

class HomeFeedsViewModel(
    private val clientManager: BlueskyClientManager,
    private val feeds: BlueskyFeeds,
    private val role: IdentityRole,
): SubViewModel() {

    private fun loadFeedsList(){
        viewModelScope.launch{
        }
    }

    private suspend fun loadFeeds(){
        val client = clientManager.getClient(role)
//        when(feeds){
//            is BlueskyContent.BlueskyTab.FeedsTab -> {
//                client.getFeedCatching(GetFeedQueryParams(feed = AtUri(tab.feedUri)))
//            }
//
//            is BlueskyContent.BlueskyTab.ListTab -> {
//                client.getListCatching(GetListQueryParams(list = AtUri(tab.listUri)))
//            }
//        }
    }
}
