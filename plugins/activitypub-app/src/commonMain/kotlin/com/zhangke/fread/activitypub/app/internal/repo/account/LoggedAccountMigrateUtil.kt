package com.zhangke.fread.activitypub.app.internal.repo.account

import com.zhangke.fread.status.model.createActivityPubProtocol
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountDao
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountEntity
import com.zhangke.fread.activitypub.app.internal.db.old.OldActivityPubLoggedAccountEntity
import com.zhangke.fread.activitypub.app.internal.db.old.OldActivityPubLoggerAccountDao
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri

object LoggedAccountMigrateUtil {

    suspend fun migrate(
        oldDao: OldActivityPubLoggerAccountDao,
        accountDao: ActivityPubLoggedAccountDao,
    ) {
        val oldAccountList = oldDao.queryAll()
            .map { convertToLoggedAccount(it) }
        if (oldAccountList.isEmpty()) return
        accountDao.insert(oldAccountList)
        oldDao.nukeTable()
    }

    private suspend fun convertToLoggedAccount(
        entity: OldActivityPubLoggedAccountEntity,
    ): ActivityPubLoggedAccountEntity {
        val account = ActivityPubLoggedAccount(
            userId = entity.userId,
            uri = FormalUri.from(entity.uri)!!,
            webFinger = entity.webFinger,
            platform = entity.platform.toPlatform(),
            baseUrl = entity.baseUrl,
            userName = entity.name,
            description = entity.description,
            avatar = entity.avatar,
            url = entity.url,
            token = entity.token,
            emojis = entity.emojis,
            followersCount = 0L,
            followingCount = 0L,
            statusesCount = 0L,
            banner = "",
            note = "",
            bot = false,
        )
        return ActivityPubLoggedAccountEntity(
            uri = account.uri.toString(),
            account = account,
            addedTimestamp = entity.addedTimestamp,
        )
    }

    private suspend fun OldActivityPubLoggedAccountEntity.BlogPlatformEntity.toPlatform(): BlogPlatform =
        BlogPlatform(
            uri = uri,
            name = name,
            description = description,
            baseUrl = baseUrl,
            thumbnail = thumbnail,
            protocol = createActivityPubProtocol(),
        )
}
