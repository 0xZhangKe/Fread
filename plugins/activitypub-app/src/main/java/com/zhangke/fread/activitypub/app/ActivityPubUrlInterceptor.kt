package com.zhangke.fread.activitypub.app

import android.content.Context
import android.provider.Settings.Global
import com.zhangke.filt.annotaions.Filt
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.HttpScheme
import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform
import okhttp3.internal.tls.OkHostnameVerifier.isAscii
import javax.inject.Inject

@Filt
class ActivityPubUrlInterceptor @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
    private val clientManager: ActivityPubClientManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
) : BrowserInterceptor {

    override suspend fun intercept(context: Context, role: IdentityRole, url: String): Boolean {
        val uri = SimpleUri.parse(url) ?: return false
        if (uri.scheme != HttpScheme.HTTP && uri.scheme != HttpScheme.HTTPS) return false
        val webFinger = parseActivityPubUser(role, uri)
        if (webFinger != null) {
            GlobalScreenNavigation.navigate(UserDetailScreen(role = role, webFinger = webFinger))
            return true
        }


        return true
    }

    private suspend fun parseActivityPubUser(role: IdentityRole, uri: SimpleUri): WebFinger? {
        val path = uri.path?.removePrefix("/") ?: return null
        if (path.isEmpty()) return null
        if (uri.queries.isNotEmpty()) return null
        if (path.contains("?") || path.contains("/")) return null
        if (!path.startsWith("@")) return null
        val accountRepo = clientManager.getClient(role).accountRepo
        val account = accountRepo.lookup(path).getOrNull() ?: return null
        return accountEntityAdapter.toWebFinger(account)
    }

    private suspend fun parsePlatform(role: IdentityRole, uri: SimpleUri): BlogPlatform? {

        val baseUrl = FormalBaseUrl.parse(uri.toString()) ?: return null
        val platform = platformRepo.getPlatform(baseUrl).getOrNull()
        return null
    }
}
