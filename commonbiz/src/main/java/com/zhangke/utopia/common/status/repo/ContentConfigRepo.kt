package com.zhangke.utopia.common.status.repo

import com.zhangke.utopia.common.status.adapter.ContentConfigAdapter
import com.zhangke.utopia.common.status.repo.db.ContentConfigDatabases
import com.zhangke.utopia.common.status.repo.db.ContentConfigEntity
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.ContentConfig.ActivityPubContent
import com.zhangke.utopia.status.model.ContentConfig.ActivityPubContent.ContentTab
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContentConfigRepo @Inject constructor(
    private val statusDatabase: ContentConfigDatabases,
    private val contentConfigAdapter: ContentConfigAdapter,
) {

    private val contentConfigDao get() = statusDatabase.getContentConfigDao()

    suspend fun getAllConfig(): List<ContentConfig> {
        return contentConfigDao.queryAllContentConfig().map { it.toContentConfig() }
    }

    fun getAllConfigFlow(): Flow<List<ContentConfig>> {
        return contentConfigDao.queryAllContentConfigFlow().map {
            it.map { it.toContentConfig() }
        }
    }

    suspend fun getConfigById(id: Long): ContentConfig? {
        return contentConfigDao.queryById(id)?.toContentConfig()
    }

    suspend fun getConfigFlowById(id: Long): Flow<ContentConfig> {
        return contentConfigDao.queryFlowById(id).map { it.toContentConfig() }
    }

    suspend fun insert(config: ContentConfig) {
        contentConfigDao.insert(contentConfigAdapter.toEntity(config))
    }

    suspend fun updateSourceList(id: Long, sourceList: List<FormalUri>) {
        contentConfigDao.updateSourceList(id, sourceList)
    }

    suspend fun updateContentName(id: Long, name: String) {
        contentConfigDao.updateName(id, name)
    }

    suspend fun updateLatestStatusId(id: Long, latestStatusId: String?) {
        contentConfigDao.updateLatestStatusId(id, latestStatusId)
    }

    suspend fun getNextOrder(): Int {
        val maxOrder = contentConfigDao.queryMaxOrder() ?: 0
        return maxOrder + 1
    }

    suspend fun reorderConfig(from: ContentConfig, to: ContentConfig) {
        if (from == to) return
        val pendingInsertList = mutableListOf<ContentConfigEntity>()
        pendingInsertList += from.toEntity(to.order)
        val allConfig = contentConfigDao.queryAllContentConfig()
        if (from.order > to.order) {
            // move up
            allConfig.filter { it.order in to.order until from.order }
                .map { it.copy(order = it.order + 1) }
                .let { pendingInsertList += it }
        } else {
            // move down
            allConfig.filter { it.order > from.order && it.order <= to.order }
                .map { it.copy(order = it.order - 1) }
                .let { pendingInsertList += it }
        }
        contentConfigDao.insertList(pendingInsertList)
    }

    // 当用户的列表发生变化时调用，用于更新用户创建的列表，多的会新增到 hidden list 中，移除的列表也会从 app 中移除。
    suspend fun updateActivityPubUserList(
        id: Long,
        allUserCreatedList: List<ContentTab.ListTimeline>,
    ) {
        val config = contentConfigDao.queryById(id)?.toContentConfig() ?: return
        if (config !is ActivityPubContent) {
            throw IllegalArgumentException("$id of config is not ActivityPubContent")
        }
        val allListIdSet = allUserCreatedList.map { it.listId }.toSet()
        val newShowingList = config.showingTabList.filter {
            if (it !is ContentTab.ListTimeline) {
                true
            } else {
                it.listId in allListIdSet
            }
        }
        val newHiddenList = mutableListOf<ContentTab>()
        config.hiddenTabList.forEach {
            if (it !is ContentTab.ListTimeline) {
                newHiddenList.add(it)
            } else if (it.listId in allListIdSet) {
                newHiddenList.add(it)
            }
        }
        val allAddedIdSet = mutableSetOf<String>().apply {
            this += newShowingList.filterIsInstance<ContentTab.ListTimeline>().map { it.listId }
            this += newHiddenList.filterIsInstance<ContentTab.ListTimeline>().map { it.listId }
        }
        var maxOrder = newHiddenList.maxBy { it.order }.order
        allUserCreatedList.filter { it.listId !in allAddedIdSet }
            .map { ContentTab.ListTimeline(it.listId, it.name, maxOrder++) }
            .let { newHiddenList += it }
        if (config.showingTabList.sortedBy { it.order } == newShowingList.sortedBy { it.order } &&
            config.hiddenTabList.sortedBy { it.order } == newHiddenList.sortedBy { it.order }) {
            return
        }
        config.copy(
            showingTabList = newShowingList,
            hiddenTabList = newHiddenList,
        ).let { contentConfigDao.insert(it.toEntity()) }
    }

    suspend fun recorderActivityPubShowingTab(
        configId: Long,
        fromTab: ContentTab,
        toTab: ContentTab,
    ) {
        if (fromTab == toTab) return
        val config = contentConfigDao.queryById(configId)?.toContentConfig() ?: return
        if (config !is ActivityPubContent) {
            throw IllegalArgumentException("$configId of config is not ActivityPubContent")
        }

        val updatedList = reorderList(
            list = config.showingTabList,
            from = fromTab,
            to = toTab,
            order = { it.order },
            updateOrder = { tab, order ->
                when (tab) {
                    is ContentTab.HomeTimeline -> tab.copy(order = order)
                    is ContentTab.LocalTimeline -> tab.copy(order = order)
                    is ContentTab.PublicTimeline -> tab.copy(order = order)
                    is ContentTab.Trending -> tab.copy(order = order)
                    is ContentTab.ListTimeline -> tab.copy(order = order)
                }
            }
        )
        val allTabList = config.showingTabList

    }

    suspend fun clearAllLastReadStatusId() {
        contentConfigDao.clearAllLastReadStatusId()
    }

    suspend fun checkNameExist(name: String): Boolean {
        return contentConfigDao.queryByName(name) != null
    }

    suspend fun deleteById(id: Long) {
        contentConfigDao.deleteById(id)
    }

    suspend fun delete(config: ContentConfig) {
        contentConfigDao.deleteById(config.id)
    }

    private fun <T> reorderList(
        list: List<T>,
        from: T,
        to: T,
        order: (T) -> Int,
        updateOrder: (T, Int) -> T,
    ): List<T> {
        val updatedList = mutableListOf<T>()
        updatedList += updateOrder(from, order(to))
        val fromOrder = order(from)
        val toOrder = order(to)
        if (fromOrder > toOrder) {
            // move up
            list.filter { order(it) in toOrder until fromOrder }
                .map { updateOrder(it, order(it) + 1) }
                .let { updatedList += it }
        } else {
            // move down
            list.filter { order(it) > order(from) && order(it) <= order(to) }
                .map { updateOrder(it, order(it) - 1) }
                .let { updatedList += it }
        }
        return updatedList
    }

    private fun ContentConfig.toEntity(order: Int = this.order): ContentConfigEntity {
        return contentConfigAdapter.toEntity(this).copy(order = order)
    }

    private fun ContentConfigEntity.toContentConfig(): ContentConfig {
        return contentConfigAdapter.toContentConfig(this)
    }
}
