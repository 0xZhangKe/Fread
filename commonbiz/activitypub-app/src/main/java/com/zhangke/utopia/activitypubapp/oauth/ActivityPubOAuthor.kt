package com.zhangke.utopia.activitypubapp.oauth

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.api.ActivityPubScope
import com.zhangke.activitypub.entry.ActivityPubAccount
import com.zhangke.activitypub.entry.ActivityPubToken
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.toast.toast
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypubapp.user.ActivityPubUser
import com.zhangke.utopia.activitypubapp.user.ActivityPubUserRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Created by ZhangKe on 2022/12/4.
 */
object ActivityPubOAuthor {

    private val oauthFlow: MutableSharedFlow<ActivityPubUser?> = MutableSharedFlow(1)
    private val oauthCodeFlow: MutableSharedFlow<String> = MutableSharedFlow()

    fun observeUserState(): Flow<ActivityPubUser?> {
        return oauthFlow
    }

    fun openOauthTipPage(oauthUrl: String, client: ActivityPubClient, isOauthFailed: Boolean) {
        OauthTipActivity.open(appContext, isOauthFailed) { confirm ->
            if (confirm) {
                startOauth(oauthUrl, client)
            }
        }
    }

    fun startOauth(oauthUrl: String, client: ActivityPubClient) {
        openOauthPage(oauthUrl)
        ApplicationScope.launch {
            val code = oauthCodeFlow.first()
            val user = try {
                val token = client.oauthRepo.getToken(code, ActivityPubScope.ALL).getOrThrow()
                val account = client.accountRepo.verifyCredentials(token.accessToken).getOrThrow()
                account.toUser(client.baseUrl, token)
            } catch (e: Exception) {
                Log.e("ActivityPubOAuthor", "OAuth failed", e)
                toast(e.message)
                null
            }
            if (user != null) {
                oauthFlow.emit(user)
                ActivityPubUserRepo.setCurrentUser(user)
            }
        }
    }

    internal fun onOauthSuccess(code: String) {
        ApplicationScope.launch { oauthCodeFlow.emit(code) }
    }

    fun openOauthPage(oauthUrl: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(appContext, Uri.parse(oauthUrl))
    }

    private fun ActivityPubAccount.toUser(
        domain: String,
        token: ActivityPubToken
    ): ActivityPubUser {
        return ActivityPubUser(
            domain = domain,
            name = username,
            id = id,
            token = token,
            avatar = avatar,
            description = note,
            homePage = url,
            selected = true,
        )
    }
}