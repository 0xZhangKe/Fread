package com.zhangke.utopia.activitypubapp.auth

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.api.ActivityPubScope
import com.zhangke.framework.toast.toast
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypubapp.account.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.utopia.activitypubapp.account.repo.ActivityPubLoggedAccountRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ZhangKe on 2022/12/4.
 */
@Singleton
class ActivityPubOAuthor @Inject constructor(
    private val repo: ActivityPubLoggedAccountRepo,
    private val accountAdapter: ActivityPubLoggedAccountAdapter
) {

    private val oauthCodeFlow: MutableSharedFlow<String> = MutableSharedFlow()

    suspend fun startOauth(oauthUrl: String, client: ActivityPubClient): Boolean {
        openOauthPage(oauthUrl)
        val code = oauthCodeFlow.first()
        val entity = try {
            val instance = client.instanceRepo.getInstanceInformation().getOrThrow()
            val token = client.oauthRepo.getToken(code, ActivityPubScope.ALL).getOrThrow()
            val account = client.accountRepo.verifyCredentials(token.accessToken).getOrThrow()
            accountAdapter.createFromAccount(instance, account, token, true)
        } catch (e: Exception) {
            toast(e.message)
            return false
        }
        repo.updateCurrentAccount(entity)
        return true
    }

    internal suspend fun onOauthSuccess(code: String) {
        oauthCodeFlow.emit(code)
    }

    private fun openOauthPage(oauthUrl: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(appContext, Uri.parse(oauthUrl))
    }
}
