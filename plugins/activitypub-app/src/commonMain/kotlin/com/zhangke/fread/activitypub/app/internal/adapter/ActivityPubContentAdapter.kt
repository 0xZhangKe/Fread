package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.status.platform.BlogPlatform

class ActivityPubContentAdapter () {

    fun createContent(
        platform: BlogPlatform,
        maxOrder: Int,
    ): ActivityPubContent {
        return ActivityPubContent(
            name = platform.name,
            baseUrl = platform.baseUrl,
            order = maxOrder + 1,
            tabList = buildInitialTabConfigList(),
            accountUri = null,
        )
    }

    private fun buildInitialTabConfigList(): List<ActivityPubContent.ContentTab> {
        val tabList = mutableListOf<ActivityPubContent.ContentTab>()
        tabList += ActivityPubContent.ContentTab.HomeTimeline(0)
        tabList += ActivityPubContent.ContentTab.LocalTimeline(1)
        tabList += ActivityPubContent.ContentTab.PublicTimeline(2)
        tabList += ActivityPubContent.ContentTab.Trending(3)
        return tabList
    }
}