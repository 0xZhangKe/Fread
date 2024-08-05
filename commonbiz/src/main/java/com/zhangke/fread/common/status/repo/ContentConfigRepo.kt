package com.zhangke.fread.common.status.repo

import com.zhangke.framework.collections.removeIndex
import com.zhangke.fread.common.status.adapter.ContentConfigAdapter
import com.zhangke.fread.common.status.repo.db.ContentConfigDatabases
import com.zhangke.fread.common.status.repo.db.ContentConfigEntity
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.ContentConfig.ActivityPubContent
import com.zhangke.fread.status.model.ContentConfig.ActivityPubContent.ContentTab
import com.zhangke.fread.status.model.dropNotExistListTab
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContentConfigRepo @Inject constructor(
    private val contentConfigDatabase: ContentConfigDatabases,
    private val contentConfigAdapter: ContentConfigAdapter,
) {

    private val contentConfigDao get() = contentConfigDatabase.getContentConfigDao()

    suspend fun getAllConfig(): List<ContentConfig> {
        return contentConfigDao.queryAllContentConfig().map { it.toContentConfig() }
    }

    fun getAllConfigFlow(): Flow<List<ContentConfig>> {
        return contentConfigDao.queryAllContentConfigFlow()
            .map { list ->
                list.map { it.toContentConfig() }
            }
    }

    fun getConfigFlow(id: Long): Flow<ContentConfig?> {
        return contentConfigDao.getContentConfigFlow(id)
            .map { it?.toContentConfig() }
    }

    suspend fun getConfigById(id: Long): ContentConfig? {
        return contentConfigDao.queryById(id)?.toContentConfig()
    }

    fun getConfigFlowById(id: Long): Flow<ContentConfig?> {
        return contentConfigDao.queryFlowById(id).map { it?.toContentConfig() }
    }

    suspend fun insertActivityPubContent(platform: BlogPlatform) {
        if (platform.protocol.notActivityPub) return
        val contentConfig = ActivityPubContent(
            id = 0,
            order = generateNextOrder(),
            name = platform.name,
            baseUrl = platform.baseUrl,
            showingTabList = buildInitialTabConfigList(),
            hiddenTabList = emptyList(),
        )
        insert(contentConfig)
    }

    private fun buildInitialTabConfigList(): List<ContentTab> {
        val tabList = mutableListOf<ContentTab>()
        tabList += ContentTab.HomeTimeline(0)
        tabList += ContentTab.LocalTimeline(1)
        tabList += ContentTab.PublicTimeline(2)
        tabList += ContentTab.Trending(3)
        return tabList
    }

    suspend fun insert(config: ContentConfig) {
        contentConfigDao.insert(contentConfigAdapter.toEntity(fixContent(config)))
    }

    suspend fun insert(configList: List<ContentConfig>) {
        configList.map { fixContent(it) }
            .map { contentConfigAdapter.toEntity(it) }
            .let { contentConfigDao.insertList(it) }
    }

    suspend fun updateSourceList(id: Long, sourceList: List<FormalUri>) {
        contentConfigDao.updateSourceList(id, sourceList)
    }

    suspend fun updateContentName(id: Long, name: String) {
        contentConfigDao.updateName(id, name)
    }

    suspend fun generateNextOrder(): Int {
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
        val localListIdSet = config.showingTabList
            .plus(config.hiddenTabList)
            .filterIsInstance<ContentTab.ListTimeline>()
            .map { it.listId }
            .toSet()
        val newShowingList = config.showingTabList.dropNotExistListTab(allListIdSet).toMutableList()
        val newHiddenList = config.hiddenTabList.dropNotExistListTab(allListIdSet)
        var maxOrder = config.showingTabList.maxByOrNull { it.order }?.order ?: 0
        allUserCreatedList.filter { it.listId !in localListIdSet }
            .map { ContentTab.ListTimeline(it.listId, it.name, maxOrder++) }
            .let { newShowingList.addAll(it) }
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
        val newShowingList = if (fromTab.order > toTab.order) {
            // move up
            config.showingTabList.map { item ->
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
            config.showingTabList.map { item ->
                if (item.order > fromTab.order && item.order <= toTab.order) {
                    item.newOrder(order = item.order - 1)
                } else if (item == fromTab) {
                    fromTab.newOrder(order = toTab.order)
                } else {
                    item
                }
            }.sortedBy { it.order }
        }
        contentConfigDao.insert(config.copy(showingTabList = newShowingList).toEntity())
    }

    suspend fun moveActivityPubTabToHide(
        configId: Long,
        tab: ContentTab,
    ) {
        val config = contentConfigDao.queryById(configId)?.toContentConfig() ?: return
        if (config !is ActivityPubContent) {
            throw IllegalArgumentException("$configId of config is not ActivityPubContent")
        }
        val tabIndex = config.showingTabList.indexOf(tab)
        if (tabIndex < 0) return
        val newShowingList = config.showingTabList.removeIndex(tabIndex)
        val newHiddenList = config.hiddenTabList + tab.newOrder(config.hiddenTabList.size)
        val newConfig = config.copy(
            showingTabList = newShowingList,
            hiddenTabList = newHiddenList,
        )
        contentConfigDao.insert(newConfig.toEntity())
    }

    suspend fun moveActivityPubTabToShowing(
        configId: Long,
        tab: ContentTab,
    ) {
        val config = contentConfigDao.queryById(configId)?.toContentConfig() ?: return
        if (config !is ActivityPubContent) {
            throw IllegalArgumentException("$configId of config is not ActivityPubContent")
        }
        val tabIndex = config.hiddenTabList.indexOf(tab)
        if (tabIndex < 0) return
        val maxShowingOrder = config.showingTabList.maxByOrNull { it.order }?.order ?: 0
        val newShowingList = config.showingTabList + tab.newOrder(maxShowingOrder + 1)
        val newHiddenList = config.hiddenTabList.removeIndex(tabIndex)
        val newConfig = config.copy(
            showingTabList = newShowingList,
            hiddenTabList = newHiddenList,
        )
        contentConfigDao.insert(newConfig.toEntity())
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

    private fun ContentConfig.toEntity(order: Int = this.order): ContentConfigEntity {
        return contentConfigAdapter.toEntity(this).copy(order = order)
    }

    private fun ContentConfigEntity.toContentConfig(): ContentConfig {
        return contentConfigAdapter.toContentConfig(this)
    }

    private fun fixContent(contentConfig: ContentConfig): ContentConfig {
        return if (contentConfig is ContentConfig.MixedContent) {
            contentConfig.copy(sourceUriList = contentConfig.sourceUriList.distinct())
        } else {
            contentConfig
        }
    }
}
