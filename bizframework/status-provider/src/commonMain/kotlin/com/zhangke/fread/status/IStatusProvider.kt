package com.zhangke.fread.status

import com.zhangke.fread.status.account.IAccountManager
import com.zhangke.fread.status.platform.IPlatformResolver
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.search.ISearchEngine
import com.zhangke.fread.status.source.IStatusSourceResolver
import com.zhangke.fread.status.status.IStatusResolver


interface IStatusProvider {

    val screenProvider: IStatusScreenProvider

    val platformResolver: IPlatformResolver

    val searchEngine: ISearchEngine

    val statusResolver: IStatusResolver

    val statusSourceResolver: IStatusSourceResolver

    val accountManager: IAccountManager
}
