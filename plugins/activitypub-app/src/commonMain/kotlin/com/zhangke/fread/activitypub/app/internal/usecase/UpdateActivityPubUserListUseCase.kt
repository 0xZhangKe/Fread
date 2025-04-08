package com.zhangke.fread.activitypub.app.internal.usecase

import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent.ContentTab
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent.ContentTab.ListTimeline
import com.zhangke.fread.common.content.FreadContentRepo
import me.tatarka.inject.annotations.Inject

class UpdateActivityPubUserListUseCase @Inject constructor(
    private val contentRepo: FreadContentRepo,
) {

    suspend operator fun invoke(
        content: ActivityPubContent,
        allUserCreatedList: List<ListTimeline>
    ) {
        val allListIdSet = allUserCreatedList.map { it.listId }.toSet()
        val localListIdSet = content.tabList
            .filterIsInstance<ListTimeline>()
            .map { it.listId }
            .toSet()
        val newTabList = content.tabList.dropNotExistListTab(allListIdSet).toMutableList()
        var maxOrder = content.tabList.maxByOrNull { it.order }?.order ?: 0
        allUserCreatedList.filter { it.listId !in localListIdSet }
            .map { ListTimeline(it.listId, it.name, maxOrder++) }
            .let { newTabList.addAll(it) }
        if (content.tabList.sortedBy { it.order } == newTabList.sortedBy { it.order }) {
            return
        }
        content.copy(tabList = newTabList).let { contentRepo.insertContent(it) }
    }

    private fun List<ContentTab>.dropNotExistListTab(
        allListId: Set<String>
    ): List<ContentTab> {
        return this.filter {
            if (it is ListTimeline) {
                it.listId in allListId
            } else {
                true
            }
        }
    }
}
