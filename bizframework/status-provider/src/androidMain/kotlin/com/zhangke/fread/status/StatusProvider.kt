package com.zhangke.fread.status

import com.zhangke.fread.status.account.AccountManager
import com.zhangke.fread.status.platform.PlatformResolver
import com.zhangke.fread.status.screen.StatusScreenProvider
import com.zhangke.fread.status.search.SearchEngine
import com.zhangke.fread.status.source.StatusSourceResolver
import com.zhangke.fread.status.status.StatusResolver
import javax.inject.Inject

/**
 * Created by ZhangKe on 2022/12/9.
 */
class StatusProvider @Inject constructor(
    providers: Set<@JvmSuppressWildcards IStatusProvider>,
) {

    val screenProvider = StatusScreenProvider(providers.map { it.screenProvider })

    val searchEngine = SearchEngine(providers.map { it.searchEngine })

    val platformResolver = PlatformResolver(providers.map { it.platformResolver })

    val statusResolver = StatusResolver(providers.map { it.statusResolver })

    val statusSourceResolver = StatusSourceResolver(providers.map { it.statusSourceResolver })

    val accountManager = AccountManager(providers.map { it.accountManager })
}

