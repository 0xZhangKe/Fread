package com.zhangke.fread.activitypub.app.internal.auth

import com.zhangke.activitypub.api.ActivityPubScope
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.toast.toast
import com.zhangke.framework.utils.Log
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubPlatformEntityAdapter
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.activitypub.app.internal.repo.application.ActivityPubApplicationRepo
import com.zhangke.fread.common.browser.OAuthHandler
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.di.ApplicationCoroutineScope
import com.zhangke.fread.common.utils.getCurrentTimeMillis
import kotlinx.coroutines.launch

/**
 * Created by ZhangKe on 2022/12/4.
 */ class ActivityPubOAuthor (
    private val repo: ActivityPubLoggedAccountRepo,
    private val applicationRepo: ActivityPubApplicationRepo,
    private val clientManager: ActivityPubClientManager,
    private val accountAdapter: ActivityPubLoggedAccountAdapter,
    private val platformEntityAdapter: ActivityPubPlatformEntityAdapter,
    private val activityPubDatabases: ActivityPubDatabases,
    private val applicationScope: ApplicationCoroutineScope,
    private val oAuthHandler: OAuthHandler,
    private val freadContentRepo: FreadContentRepo,
) {

    internal fun startOauth(
        baseUrl: FormalBaseUrl,
    ) = applicationScope.launch {
        val app = applicationRepo.getApplicationByBaseUrl(baseUrl)
        if (app == null) {
            toast("Application not registered")
            return@launch
        }
        val client = clientManager.getClientNoAccount(baseUrl)
        val oauthUrl = client.oauthRepo.buildOAuthUrl(
            baseUrl = baseUrl.toString(),
            clientId = app.clientId,
            redirectUri = app.redirectUri,
        )
        val code = try {
            oAuthHandler.startOAuth(oauthUrl)
        } catch (e: Exception) {
            toast(e.message)
            return@launch
        }
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
            accountAdapter.createFromAccount(baseUrl, instance, accountEntity, token)
        } catch (e: Exception) {
            toast(e.message)
            return@launch
        }
        insertAccount(account)
    }

    private suspend fun insertAccount(account: ActivityPubLoggedAccount) {
        repo.insert(account, getCurrentTimeMillis())
        val contentList = freadContentRepo.getAllContent().filterIsInstance<ActivityPubContent>()
        val addedContent = contentList.firstOrNull {
            account.baseUrl == it.baseUrl && it.accountUri == null
        }
        Log.d("T_TEST") { "addedContent ${addedContent?.id}" }
        if (addedContent != null) {
            freadContentRepo.delete(addedContent.id)
            freadContentRepo.insertContent(addedContent.copy(accountUri = account.uri).also {
                Log.d("T_TEST") { "insert ${it.id}" }
            })
        }
    }
}