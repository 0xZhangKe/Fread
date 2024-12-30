package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.feed.GeneratorView
import app.bsky.graph.GetListsQueryParams
import app.bsky.graph.ListView
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.client.BlueskyClient
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtIdentifier
import sh.christian.ozone.api.Did

class UpdateHomeTabUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val getFeeds: GetFollowingFeedsUseCase,
    private val contentRepo: FreadContentRepo,
) {

    suspend operator fun invoke(
        contentId: String,
        role: IdentityRole,
        accountDid: String,
    ) {
        val content = contentRepo.getContent(contentId)
            ?.takeIf { it is BlueskyContent }
            ?.let { it as BlueskyContent } ?: return
        val client = clientManager.getClient(role)

        val listListResult = getAllList(client, Did(accountDid))
        if (listListResult.isFailure) return
        val listViewList = listListResult.getOrThrow()
        val newListTabs = mergeListTab(content, listViewList)

        val feedsListResult = getFeeds.invoke(role)
        if (feedsListResult.isFailure) return
        val feedsList = feedsListResult.getOrThrow()
        val newFeedsTabs = mergeFeedsTab(content, feedsList)

        val newTabs = (newListTabs + newFeedsTabs).sortedBy { it.order }
        contentRepo.insertContent(content.copy(tabList = newTabs))
    }

    private fun mergeListTab(
        content: BlueskyContent,
        allListTab: List<ListView>,
    ): List<BlueskyContent.BlueskyTab.ListTab> {
        var maxOrder = content.tabList.maxOfOrNull { it.order } ?: 0
        val localListTab = content.tabList.filterIsInstance<BlueskyContent.BlueskyTab.ListTab>()
        val newList = mutableListOf<BlueskyContent.BlueskyTab.ListTab>()
        localListTab.forEach { tab ->
            if (allListTab.any { it.uri.atUri == tab.listUri }) {
                newList += tab
            }
        }
        allListTab.forEach { tab ->
            if (!newList.any { it.listUri == tab.uri.atUri }) {
                newList += tab.toTab(maxOrder++)
            }
        }
        return newList
    }

    private fun mergeFeedsTab(
        content: BlueskyContent,
        allFeedsTab: List<GeneratorView>,
    ): List<BlueskyContent.BlueskyTab.FeedsTab> {
        var maxOrder = content.tabList.maxOfOrNull { it.order } ?: 0
        val localFeedsTabs = content.tabList.filterIsInstance<BlueskyContent.BlueskyTab.FeedsTab>()
        val newList = mutableListOf<BlueskyContent.BlueskyTab.FeedsTab>()
        localFeedsTabs.forEach { tab ->
            if (allFeedsTab.any { it.uri.atUri == tab.feedUri }) {
                newList += tab
            }
        }
        allFeedsTab.forEach { tab ->
            if (!newList.any { it.feedUri == tab.uri.atUri }) {
                newList += BlueskyContent.BlueskyTab.FeedsTab(
                    feedUri = tab.uri.atUri,
                    title = tab.displayName,
                    order = maxOrder++,
                    hide = false,
                )
            }
        }
        return newList
    }

    private suspend fun getAllList(
        client: BlueskyClient,
        actor: AtIdentifier,
    ): Result<List<ListView>> {
        val list = mutableListOf<ListView>()
        var cursor: String? = null
        var repeatCount = 0
        while (true) {
            val result =
                client.getListsCatching(GetListsQueryParams(actor = actor, cursor = cursor))
            if (result.isFailure) return Result.failure(result.exceptionOrThrow())
            cursor = result.getOrThrow().cursor
            list += result.getOrThrow().lists
            repeatCount++
            if (cursor.isNullOrBlank() || repeatCount > 30) {
                break
            }
        }
        return Result.success(list)
    }

    private fun ListView.toTab(order: Int): BlueskyContent.BlueskyTab.ListTab {
        return BlueskyContent.BlueskyTab.ListTab(
            listUri = this.uri.atUri,
            title = this.name,
            order = order,
            hide = false,
        )
    }
}
