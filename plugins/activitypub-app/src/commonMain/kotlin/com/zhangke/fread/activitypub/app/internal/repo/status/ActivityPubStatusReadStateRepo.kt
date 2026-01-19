package com.zhangke.fread.activitypub.app.internal.repo.status

import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusReadStateDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusReadStateEntity
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.status.model.PlatformLocator

class ActivityPubStatusReadStateRepo (
    activityPubStatusReadStateDatabases: ActivityPubStatusReadStateDatabases,
) {

    private val readStateDao = activityPubStatusReadStateDatabases.getDao()

    suspend fun getLatestReadId(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String? = null
    ): String? {
        return if (listId == null) {
            readStateDao.query(
                locator = locator,
                type = type,
            )?.latestReadId
        } else {
            readStateDao.queryList(
                locator = locator,
                type = type,
                listId = listId,
            )?.latestReadId
        }
    }

    suspend fun updateLatestReadId(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String? = null,
        latestReadId: String
    ) {
        val entity = ActivityPubStatusReadStateEntity(
            locator = locator,
            type = type,
            listId = listId.orEmpty(),
            latestReadId = latestReadId,
        )
        readStateDao.update(entity)
    }
}