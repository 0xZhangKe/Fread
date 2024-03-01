package com.zhangke.utopia.activitypub.app.internal.usecase.content

import com.zhangke.activitypub.entities.ActivityPubListEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.ContentConfig.ActivityPubContent.ContentTab
import javax.inject.Inject

class CreateContentConfigUseCase @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
) {

    suspend operator fun invoke(
        baseUrl: FormalBaseUrl,
        title: String,
        userList: List<ActivityPubListEntity>,
    ): ContentConfig.ActivityPubContent {
        val order = contentConfigRepo.getNextOrder()
        return ContentConfig.ActivityPubContent(
            id = 0,
            order = order,
            name = title,
            baseUrl = baseUrl,
            showingTabList = buildInitialTabConfigList(userList),
            hiddenTabList = emptyList(),
        )
    }

    private fun buildInitialTabConfigList(userList: List<ActivityPubListEntity>): List<ContentTab> {
        val tabList = mutableListOf<ContentTab>()
        tabList += ContentTab.HomeTimeline(0)
        userList.forEachIndexed { index, entity ->
            tabList += createUserListTab(entity, index + 1)
        }
        tabList += ContentTab.LocalTimeline(tabList.size)
        tabList += ContentTab.PublicTimeline(tabList.size)
        tabList += ContentTab.Trending(tabList.size)
        return tabList
    }

    private fun createUserListTab(entity: ActivityPubListEntity, order: Int): ContentTab {
        return ContentTab.ListTimeline(
            listId = entity.id,
            name = entity.title,
            order = order,
        )
    }
}
