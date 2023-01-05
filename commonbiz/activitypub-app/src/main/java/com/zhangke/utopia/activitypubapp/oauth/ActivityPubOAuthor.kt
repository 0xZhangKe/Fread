package com.zhangke.utopia.activitypubapp.oauth

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import com.zhangke.activitypub.ActivityPubApplication
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.api.ActivityPubScope
import com.zhangke.activitypub.entry.ActivityPubAccount
import com.zhangke.activitypub.entry.ActivityPubToken
import com.zhangke.framework.architect.coroutines.ApplicationScope
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

    fun startOauth(activity: Activity, client: ActivityPubClient) {
        openOauthPage(activity, client.application)
        ApplicationScope.launch {
            val code = oauthCodeFlow.first()
            val user = try {
                val token = client.oauthRepo.getToken(code, ActivityPubScope.ALL).getOrThrow()
                val account = client.accountRepo.verifyCredentials().getOrThrow()
                account.toUser(client.baseUrl, token)
            } catch (e: Exception) {
                Log.e("ActivityPubOAuthor", "OAuth failed", e)
                Toast.makeText(appContext, e.message, Toast.LENGTH_SHORT).show()
                null
            }
            if (user != null) {
                oauthFlow.emit(user)
                ActivityPubUserRepo.insertUser(user)
                ActivityPubUserRepo.insertUser(user)
            }
        }
    }

    internal fun onOauthSuccess(code: String) {
        ApplicationScope.launch { oauthCodeFlow.emit(code) }
    }

    private fun openOauthPage(activity: Activity, app: ActivityPubApplication) {
//        val oauthUrl = "https://m.cmx.im/oauth/authorize" +
//                "?response_type=code" +
//                "&client_id=KHGSFM7oZY2_ZhaQRo25DfBRNwERZy7_iqZ_HjA5Sp8" +
//                "&redirect_uri=utopia://oauth.utopia" +
//                "&scope=read+write+follow+push"
        val oauthUrl = "https://m.cmx.im/oauth/authorize" +
                "?response_type=code" +
                "&client_id=${app.clientId}" +
                "&redirect_uri=${app.redirectUri}" +
                "&scope=read+write+follow+push"

        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.launchUrl(activity, Uri.parse(oauthUrl))
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
            selected = false,
        )
    }
}