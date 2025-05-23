package com.zhangke.fread.commonbiz.shared.repo

import com.zhangke.fread.commonbiz.shared.db.SelectedAccountPublishing
import com.zhangke.fread.commonbiz.shared.db.SelectedAccountPublishingDatabase
import me.tatarka.inject.annotations.Inject

class SelectedAccountPublishingRepo @Inject constructor(
    private val database: SelectedAccountPublishingDatabase,
) {

    suspend fun getAll(): List<String> {
        return database.dao().getAll().map { it.accountUri }
    }

    suspend fun replace(items: List<String>) {
        database.dao().let {
            it.deleteTable()
            it.insert(items.map { SelectedAccountPublishing(it) })
        }
    }

    suspend fun deleteTable() {
        database.dao().deleteTable()
    }
}
