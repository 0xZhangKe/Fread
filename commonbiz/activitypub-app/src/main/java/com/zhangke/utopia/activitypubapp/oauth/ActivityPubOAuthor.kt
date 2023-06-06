package com.zhangke.utopia.activitypubapp.oauth

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.api.ActivityPubScope
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.toast.toast
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypubapp.adapter.ActivityPubAccountAdapter
import com.zhangke.utopia.activitypubapp.user.repo.ActivityPubUserRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Singleton

/**
 * Created by ZhangKe on 2022/12/4.
 */
@Singleton
class ActivityPubOAuthor(
    private val repo: ActivityPubUserRepo,
    private val accountAdapter: ActivityPubAccountAdapter
) {

    private val oauthCodeFlow: MutableSharedFlow<String> = MutableSharedFlow()

    suspend fun startOauth(oauthUrl: String, client: ActivityPubClient): Boolean {
        openOauthPage(oauthUrl)
        val code = oauthCodeFlow.first()
        val entity = try {
            val instance = client.instanceRepo.getInstanceInformation().getOrThrow()
            val token = client.oauthRepo.getToken(code, ActivityPubScope.ALL).getOrThrow()
            val account = client.accountRepo.verifyCredentials(token.accessToken).getOrThrow()
            accountAdapter.createEntity(instance, account, token, true)
        } catch (e: Exception) {
            toast(e.message)
            return false
        }
        repo.updateCurrentUser(entity)
        return true
    }

    internal fun onOauthSuccess(code: String) {
        ApplicationScope.launch { oauthCodeFlow.emit(code) }
    }

    private fun openOauthPage(oauthUrl: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(appContext, Uri.parse(oauthUrl))
    }
}
