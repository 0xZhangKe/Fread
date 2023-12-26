package com.zhangke.utopia.activitypub.app.internal.repo

import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.db.WebFingerBaseurlToIdEntity
import javax.inject.Inject

class WebFingerBaseUrlToUserIdRepo @Inject constructor(
    activityPubDatabases: ActivityPubDatabases,
    private val clientManager: ActivityPubClientManager,
) {

    private val userIdDao = activityPubDatabases.getUserIdDao()

    suspend fun getUserId(webFinger: WebFinger, baseUrl: String): Result<String> {
        val userIdFromLocal = userIdDao.queryUserId(webFinger, baseUrl)
        if (userIdFromLocal.isNullOrEmpty().not()) return Result.success(userIdFromLocal!!)
        val result = clientManager.getClient(baseUrl).accountRepo.lookup(webFinger.toString())
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val account = result.getOrNull()
            ?: return Result.failure(IllegalArgumentException("Can't find $webFinger in $baseUrl"))
        insert(webFinger, baseUrl, account.id)
        return Result.success(account.id)
    }

    suspend fun insert(webFinger: WebFinger, baseUrl: String, userId: String) {
        userIdDao.insert(WebFingerBaseurlToIdEntity(webFinger, baseUrl, userId))
    }

    suspend fun delete(webFinger: WebFinger, baseUrl: String) {
        userIdDao.delete(webFinger, baseUrl)
    }
}
