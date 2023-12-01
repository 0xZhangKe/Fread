package com.zhangke.utopia.status

import com.zhangke.utopia.status.account.AccountManager
import com.zhangke.utopia.status.account.IAccountManager
import com.zhangke.utopia.status.emoji.CustomEmojiProvider
import com.zhangke.utopia.status.emoji.ICustomEmojiProvider
import com.zhangke.utopia.status.platform.IPlatformResolver
import com.zhangke.utopia.status.platform.PlatformResolver
import com.zhangke.utopia.status.search.IUtopiaSearchEngine
import com.zhangke.utopia.status.search.SearchEngine
import com.zhangke.utopia.status.source.IStatusSourceResolver
import com.zhangke.utopia.status.source.StatusSourceResolver
import com.zhangke.utopia.status.status.IStatusResolver
import com.zhangke.utopia.status.status.StatusResolver
import javax.inject.Inject

/**
 * Created by ZhangKe on 2022/12/9.
 */
class StatusProvider @Inject constructor(
    providers: Set<@JvmSuppressWildcards IStatusProvider>,
) {

    val searchEngine = SearchEngine(providers.map { it.searchEngine })

    val platformResolver = PlatformResolver(providers.map { it.platformResolver })

    val statusResolver = StatusResolver(providers.map { it.statusResolver })

    val statusSourceResolver = StatusSourceResolver(providers.map { it.statusSourceResolver })

    val accountManager = AccountManager(providers.map { it.accountManager })

    val customEmojiProvider = CustomEmojiProvider(providers.map { it.customEmojiProvider })

}

interface IStatusProvider {

    val platformResolver: IPlatformResolver

    val searchEngine: IUtopiaSearchEngine

    val statusResolver: IStatusResolver

    val statusSourceResolver: IStatusSourceResolver

    val accountManager: IAccountManager

    val customEmojiProvider: ICustomEmojiProvider
}
