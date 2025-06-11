package com.zhangke.fread.common.content

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.fread.common.db.old.ContentConfigDatabases
import com.zhangke.fread.common.db.old.OldFreadContentDatabase
import com.zhangke.fread.common.db.old.OldFreadContentEntity
import com.zhangke.fread.common.status.adapter.ContentConfigAdapter
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.FreadContent
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import kotlin.collections.map

class FreadContentRepo @Inject constructor(
    database: OldFreadContentDatabase,
    private val contentConfigDatabases: ContentConfigDatabases,
    private val statusProvider: StatusProvider,
    private val contentConfigAdapter: ContentConfigAdapter,
) {

    private val dao = database.contentDao()

    init {
        migrateOldDb()
    }

    fun getAllContentFlow() =
        dao.queryAllFlow().map { list -> list.map { it.content }.sortedBy { it.order } }

    fun getContentFlow(id: String) = dao.queryFlow(id).mapNotNull { it?.content }

    suspend fun getAllContent(): List<FreadContent> {
        return dao.queryAll().map { it.content }.sortedBy { it.order }
    }

    suspend fun getContent(id: String): FreadContent? {
        return dao.query(id)?.content
    }

    suspend fun insertContent(content: FreadContent) {
        dao.insert(content.toEntity())
    }

    suspend fun insertAll(content: List<FreadContent>) {
        dao.insertAll(content.map { it.toEntity() })
    }

    suspend fun delete(id: String) {
        dao.delete(id)
    }

    suspend fun getMaxOrder(): Int {
        return getAllContent().maxOfOrNull { it.order } ?: 0
    }

    suspend fun checkNameExist(name: String): Boolean {
        return getAllContent().any { it.name == name }
    }

    suspend fun reorderConfig(from: FreadContent, to: FreadContent) {
        if (from == to) return
        val pendingInsertList = mutableListOf<FreadContent>()
        pendingInsertList += from.newOrder(to.order)
        val allConfig = getAllContent()
        if (from.order > to.order) {
            // move up
            allConfig.filter { it.order in to.order until from.order }
                .map { it.newOrder(it.order + 1) }
                .let { pendingInsertList += it }
        } else {
            // move down
            allConfig.filter { it.order > from.order && it.order <= to.order }
                .map { it.newOrder(it.order - 1) }
                .let { pendingInsertList += it }
        }
        insertAll(pendingInsertList)
    }

    private fun FreadContent.toEntity(): OldFreadContentEntity {
        return OldFreadContentEntity(
            id = this.id,
            content = this,
        )
    }

    private fun migrateOldDb() {
        ApplicationScope.launch {
            val contentConfigList = contentConfigDatabases.getContentConfigDao()
                .queryAllContentConfig()
            if (contentConfigList.isEmpty()) return@launch
            contentConfigList
                .map(contentConfigAdapter::toContentConfig)
                .mapNotNull { it.toContent() }
                .let { insertAll(it) }
            contentConfigDatabases.getContentConfigDao().deleteTable()
        }
    }

    private fun ContentConfig.toContent(): FreadContent? {
        if (this is ContentConfig.MixedContent) {
            return com.zhangke.fread.status.content.MixedContent(
                id = this.id.toString(),
                order = this.order,
                name = this.name,
                sourceUriList = this.sourceUriList,
            )
        }
        return statusProvider.contentManager.restoreContent(this)
    }
}
