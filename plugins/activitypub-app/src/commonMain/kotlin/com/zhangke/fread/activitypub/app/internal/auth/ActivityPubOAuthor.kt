package com.zhangke.fread.activitypub.app.internal.auth

import com.zhangke.activitypub.api.ActivityPubScope
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.toast.toast
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubPlatformEntityAdapter
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.activitypub.app.internal.repo.application.ActivityPubApplicationRepo
import com.zhangke.fread.common.di.ApplicationCoroutineScope
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.ext.getCurrentTimeMillis
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

/**
 * Created by ZhangKe on 2022/12/4.
 */
@ApplicationScope
class ActivityPubOAuthor @Inject constructor(
    private val repo: ActivityPubLoggedAccountRepo,
    private val applicationRepo: ActivityPubApplicationRepo,
    private val clientManager: ActivityPubClientManager,
    private val accountAdapter: ActivityPubLoggedAccountAdapter,
    private val platformEntityAdapter: ActivityPubPlatformEntityAdapter,
    private val activityPubDatabases: ActivityPubDatabases,
    private val applicationScope: ApplicationCoroutineScope,
) {

    private val oauthCodeFlow: MutableSharedFlow<String> = MutableSharedFlow()

    fun startOauth(
        baseUrl: FormalBaseUrl,
        openOauthPage: (String) -> Unit,
    ) = applicationScope.launch {
        val app = applicationRepo.getApplicationByBaseUrl(baseUrl)
        if (app == null) {
            toast("Application not registered")
            return@launch
        }
        val client = clientManager.getClient(IdentityRole(null, baseUrl))
        val oauthUrl = client.oauthRepo.buildOAuthUrl(
            baseUrl = baseUrl.toString(),
            clientId = app.clientId,
            redirectUri = app.redirectUri,
        )
        openOauthPage(oauthUrl)
        val code = oauthCodeFlow.first()
        val account = try {
            val instance = client.instanceRepo.getInstanceInformation().getOrThrow()
            activityPubDatabases.getPlatformDao()
                .insert(platformEntityAdapter.toEntity(baseUrl, instance))
            val token = client.oauthRepo.getToken(
                code = code,
                clientId = app.clientId,
                clientSecret = app.clientSecret,
                redirectUri = app.redirectUri,
                scopeList = ActivityPubScope.ALL,
            ).getOrThrow()
            val accountEntity =
                client.accountRepo.verifyCredentials(token.accessToken).getOrThrow()
            accountAdapter.createFromAccount(instance, accountEntity, token)
        } catch (e: Exception) {
            toast(e.message)
            return@launch
        }
        repo.insert(account, getCurrentTimeMillis())
    }

    internal suspend fun onOauthSuccess(code: String) {
        oauthCodeFlow.emit(code)
    }
}
