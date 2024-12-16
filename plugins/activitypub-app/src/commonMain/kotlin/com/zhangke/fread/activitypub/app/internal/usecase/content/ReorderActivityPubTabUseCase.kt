package com.zhangke.fread.activitypub.app.internal.usecase.content

import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.common.content.FreadContentRepo
import me.tatarka.inject.annotations.Inject

class ReorderActivityPubTabUseCase @Inject constructor(
    private val contentRepo: FreadContentRepo,
) {

    suspend operator fun invoke(
        content: ActivityPubContent,
        fromTab: ActivityPubContent.ContentTab,
        toTab: ActivityPubContent.ContentTab,
    ) {
        if (fromTab == toTab) return
        val newShowingList = if (fromTab.order > toTab.order) {
            // move up
            content.tabList.map { item ->
                if (item.order in toTab.order until fromTab.order) {
                    item.newOrder(item.order + 1)
                } else if (item == fromTab) {
                    fromTab.newOrder(order = toTab.order)
                } else {
                    item
                }
            }.sortedBy { it.order }
        } else {
            // move down
            content.tabList.map { item ->
                if (item.order > fromTab.order && item.order <= toTab.order) {
                    item.newOrder(order = item.order - 1)
                } else if (item == fromTab) {
                    fromTab.newOrder(order = toTab.order)
                } else {
                    item
                }
            }.sortedBy { it.order }
        }
        contentRepo.insertContent(content.copy(tabList = newShowingList))
    }
}
