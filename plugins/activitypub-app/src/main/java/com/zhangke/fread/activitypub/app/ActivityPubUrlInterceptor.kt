package com.zhangke.fread.activitypub.app

import android.content.Context
import com.zhangke.filt.annotaions.Filt
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.network.HttpScheme
import com.zhangke.framework.network.SimpleUri
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status
import javax.inject.Inject

@Filt
class ActivityPubUrlInterceptor @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
    private val clientManager: ActivityPubClientManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
) : BrowserInterceptor {

    override suspend fun intercept(context: Context, role: IdentityRole, url: String): Boolean {
        val uri = SimpleUri.parse(url) ?: return false
        if (HttpScheme.validate(uri.scheme.orEmpty())) return false
        val status = parseStatus(role, uri)
        if (status != null) {
            GlobalScreenNavigation.navigate(StatusContextScreen(role = role, status = status))
            return true
        }
        val webFinger = parseActivityPubUser(role, uri)
        if (webFinger != null) {
            GlobalScreenNavigation.navigate(UserDetailScreen(role = role, webFinger = webFinger))
            return true
        }
        val platform = parsePlatform(uri)
        if (platform != null) {
            GlobalScreenNavigation.navigate(InstanceDetailScreen(baseUrl = platform.baseUrl))
            return true
        }
        return false
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

    private suspend fun parseStatus(role: IdentityRole, uri: SimpleUri): Status? {
        if (uri.queries.isNotEmpty()) return null
        val baseUrl = FormalBaseUrl.parse(uri.toString()) ?: return null
        val path = uri.path.toString().removePrefix("/")
        val array = path.split("/")
        if (array.size != 2) return null
        val acct = array[0]
        val statusId = array[1]
        if (!acct.startsWith("@")) return null
        val statusRepo = clientManager.getClient(role).statusRepo
        val platform = platformRepo.getPlatform(baseUrl).getOrNull() ?: return null
        val statusEntity = statusRepo.getStatuses(statusId).getOrNull() ?: return null
        return activityPubStatusAdapter.toStatus(statusEntity, platform)
    }

    private suspend fun parsePlatform(uri: SimpleUri): BlogPlatform? {
        val baseUrl = FormalBaseUrl.parse(uri.toString()) ?: return null
        if (uri.queries.isNotEmpty()) return null
        if (!uri.path?.removePrefix("/").isNullOrEmpty()) return null
        return platformRepo.getPlatform(baseUrl).getOrNull()
    }
}
