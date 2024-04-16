package com.zhangke.utopia.activitypub.app.internal.repo

import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.db.WebFingerBaseurlToIdEntity
import com.zhangke.utopia.activitypub.app.internal.usecase.ResolveBaseUrlUseCase
import com.zhangke.utopia.status.model.IdentityRole
import javax.inject.Inject

class WebFingerBaseUrlToUserIdRepo @Inject constructor(
    private val resolveBaseUrl: ResolveBaseUrlUseCase,
    activityPubDatabases: ActivityPubDatabases,
    private val clientManager: ActivityPubClientManager,
) {

    private val userIdDao = activityPubDatabases.getUserIdDao()

    suspend fun getUserId(webFinger: WebFinger, role: IdentityRole): Result<String> {
        val baseUrl = resolveBaseUrl(role)
        val userIdFromLocal = userIdDao.queryUserId(webFinger, baseUrl)
        if (userIdFromLocal.isNullOrEmpty().not()) return Result.success(userIdFromLocal!!)
        val result = lookupAccount(webFinger, role)
        if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        val account = result.getOrNull()
            ?: return Result.failure(IllegalArgumentException("Can't find $webFinger in $baseUrl"))
        insert(webFinger, role, account.id)
        return Result.success(account.id)
    }

    private suspend fun lookupAccount(
        webFinger: WebFinger,
        role: IdentityRole,
    ): Result<ActivityPubAccountEntity?> {
        // 先通过 WebFinger 查找，找不到则通过 name 查找。
        // 例如这个用户：https://m.cmx.im/@b0rk@jvns.ca
        // 目前我们的客户端创建的 WebFinger 为：@b0rk@social.jvns.ca
        // 因为我们的客户端的 WebFinger 中的 host 是实际上真正的 host。
        // 但这个用户在自己服务器上的 WebFinger 是 @b0rk@jvns.ca，
        // 直接使用我们的 WebFinger 是查找不到用户的，但是通过用户名可以查找到。
        // 类似的，JW 大佬的账号也是这样，这是个普遍情况。
        val accountRepo = clientManager.getClient(role).accountRepo
        val result = accountRepo.lookup(webFinger.toString())
        if (result.isSuccess) return result
        return accountRepo.lookup(webFinger.name)
    }

    suspend fun insert(webFinger: WebFinger, role: IdentityRole, userId: String) {
        val baseUrl = resolveBaseUrl(role)
        userIdDao.insert(WebFingerBaseurlToIdEntity(webFinger, baseUrl, userId))
    }

    suspend fun delete(webFinger: WebFinger, role: IdentityRole) {
        val baseUrl = resolveBaseUrl(role)
        userIdDao.delete(webFinger, baseUrl.toString())
    }
}
