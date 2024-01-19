package com.zhangke.utopia.activitypub.app.internal.baseurl

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import javax.inject.Inject

class BaseUrlManager @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
) {

    private val defaultBaseUrl: FormalBaseUrl = FormalBaseUrl.parse("https://mastodon.online")!!

    /**
     * 获取当前已经登陆的账号的 BaseUrl，
     * 如果有多个账号，那么使用第一个账号的 BaseUrl。
     */
    suspend fun getLoggedBaseUrl(): FormalBaseUrl? {
        return accountManager.getAllLoggedAccount().firstOrNull()?.platform?.baseUrl
    }

    /**
     * 决定一个 BaseUrl。
     * 如果当前有已登陆用户，且其中包含 suggestBaseUrl，那直接返回 suggestBaseUrl。
     * 否则返回已登陆中的第一个。
     * 如果 suggestBaseUrl 不为空则使用 suggestBaseUrl。
     * 否则返回 defaultBaseUrl。
     */
    suspend fun decideBaseUrl(suggestBaseUrl: FormalBaseUrl? = null): FormalBaseUrl {
        val allLoggedAccount = accountManager.getAllLoggedAccount()
        allLoggedAccount
            .firstOrNull { it.platform.baseUrl == suggestBaseUrl }
            ?.let { return it.platform.baseUrl }
        allLoggedAccount.firstOrNull()?.let { return it.platform.baseUrl }
        suggestBaseUrl?.let { return it }
        return defaultBaseUrl
    }
}
