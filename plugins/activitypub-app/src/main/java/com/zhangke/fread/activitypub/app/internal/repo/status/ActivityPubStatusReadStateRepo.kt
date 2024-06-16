package com.zhangke.fread.activitypub.app.internal.repo.status

import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusReadStateDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusReadStateEntity
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.status.model.IdentityRole
import javax.inject.Inject

class ActivityPubStatusReadStateRepo @Inject constructor(
    private val activityPubStatusReadStateDatabases: ActivityPubStatusReadStateDatabases,
) {

    private val readStateDao = activityPubStatusReadStateDatabases.getDao()

    suspend fun getLatestReadId(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String? = null
    ): String? {
        return if (listId == null) {
            readStateDao.query(
                role = role,
                type = type,
            )?.latestReadId
        } else {
            readStateDao.queryList(
                role = role,
                type = type,
                listId = listId,
            )?.latestReadId
        }
    }

    suspend fun updateLatestReadId(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String? = null,
        latestReadId: String
    ) {
        val entity = ActivityPubStatusReadStateEntity(
            role = role,
            type = type,
            listId = listId.orEmpty(),
            latestReadId = latestReadId,
        )
        readStateDao.update(entity)
    }
}