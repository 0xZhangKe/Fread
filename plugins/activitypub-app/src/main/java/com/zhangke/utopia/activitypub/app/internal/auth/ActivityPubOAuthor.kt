package com.zhangke.utopia.activitypub.app.internal.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.zhangke.activitypub.api.ActivityPubScope
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.toast.toast
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPlatformEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.utopia.activitypub.app.internal.repo.application.ActivityPubApplicationRepo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ZhangKe on 2022/12/4.
 */
@Singleton
class ActivityPubOAuthor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: ActivityPubLoggedAccountRepo,
    private val applicationRepo: ActivityPubApplicationRepo,
    private val clientManager: ActivityPubClientManager,
    private val accountAdapter: ActivityPubLoggedAccountAdapter,
    private val platformEntityAdapter: ActivityPubPlatformEntityAdapter,
    private val activityPubDatabases: ActivityPubDatabases,
) {

    private val oauthCodeFlow: MutableSharedFlow<String> = MutableSharedFlow()

    suspend fun startOauth(baseUrl: FormalBaseUrl): Result<Boolean> {
        val oauthDeferred = ApplicationScope.async {
            val app =
                applicationRepo.getApplicationByBaseUrl(baseUrl) ?: return@async Result.failure(
                    IllegalStateException("Can not get application info by $baseUrl")
                )
            val client = clientManager.getClient(baseUrl)
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
                return@async Result.failure(e)
            }
            repo.insert(account)
            return@async Result.success(true)
        }
        return oauthDeferred.await()
    }

    internal suspend fun onOauthSuccess(code: String) {
        oauthCodeFlow.emit(code)
    }

    private fun openOauthPage(oauthUrl: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(context, Uri.parse(oauthUrl))
    }
}
