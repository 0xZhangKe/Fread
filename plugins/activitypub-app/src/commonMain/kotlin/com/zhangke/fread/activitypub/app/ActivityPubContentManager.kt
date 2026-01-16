package com.zhangke.fread.activitypub.app

import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.screen.add.AddActivityPubContentScreenKey
import com.zhangke.fread.status.content.AddContentAction
import com.zhangke.fread.status.content.IContentManager
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class ActivityPubContentManager @Inject constructor() : IContentManager {

    override suspend fun addContent(
        platform: BlogPlatform,
        action: AddContentAction,
    ) {
        if (platform.protocol.notActivityPub) return
        action.onFinishPage()
        action.onOpenNewPage(AddActivityPubContentScreenKey(platform))
    }

    override fun restoreContent(config: ContentConfig): FreadContent? {
        if (config !is ContentConfig.ActivityPubContent) return null
        return ActivityPubContent(
            order = config.order,
            name = config.name,
            baseUrl = config.baseUrl,
            tabList = buildList {
                addAll(config.showingTabList.map { it.toTab(false) })
                addAll(config.hiddenTabList.map { it.toTab(true) })
            },
            accountUri = null,
        )
    }

    private fun ContentConfig.ActivityPubContent.ContentTab.toTab(
        hide: Boolean,
    ): ActivityPubContent.ContentTab {
        return when (this) {
            is ContentConfig.ActivityPubContent.ContentTab.HomeTimeline -> ActivityPubContent.ContentTab.HomeTimeline(
                order = this.order,
                hide = hide,
            )

            is ContentConfig.ActivityPubContent.ContentTab.LocalTimeline -> ActivityPubContent.ContentTab.LocalTimeline(
                order = this.order,
                hide = hide,
            )

            is ContentConfig.ActivityPubContent.ContentTab.ListTimeline -> ActivityPubContent.ContentTab.ListTimeline(
                order = this.order,
                listId = this.listId,
                name = this.name,
                hide = hide,
            )

            is ContentConfig.ActivityPubContent.ContentTab.PublicTimeline -> ActivityPubContent.ContentTab.PublicTimeline(
                order = this.order,
                hide = hide,
            )

            is ContentConfig.ActivityPubContent.ContentTab.Trending -> ActivityPubContent.ContentTab.Trending(
                order = this.order,
                hide = hide,
            )
        }
    }
}
