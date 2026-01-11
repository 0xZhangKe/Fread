package com.zhangke.fread.rss

import com.zhangke.fread.rss.internal.adapter.BlogAuthorAdapter
import com.zhangke.fread.rss.internal.adapter.RssStatusAdapter
import com.zhangke.fread.rss.internal.platform.RssPlatformTransformer
import com.zhangke.fread.rss.internal.repo.RssRepo
import com.zhangke.fread.rss.internal.repo.RssStatusRepo
import com.zhangke.fread.rss.internal.rss.RssFetcher
import com.zhangke.fread.rss.internal.rss.RssParserWrapper
import com.zhangke.fread.rss.internal.screen.source.RssSourceViewModel
import com.zhangke.fread.rss.internal.source.RssSourceTransformer
import com.zhangke.fread.rss.internal.uri.RssUriTransformer
import com.zhangke.fread.rss.internal.webfinger.RssSourceWebFingerTransformer
import com.zhangke.fread.status.IStatusProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val rssModule = module {

    createPlatformModule()

    singleOf(::RssRepo)
    singleOf(::RssParserWrapper)
    singleOf(::RssFetcher)

    factoryOf(::RssContentManager)
    factoryOf(::RssScreenProvider)
    factoryOf(::RssSearchEngine)
    factoryOf(::RssAccountManager)
    factoryOf(::RssStatusResolver)
    factoryOf(::RssStatusSourceResolver)
    factoryOf(::RssNotificationResolver)
    factoryOf(::RssPublishManager)
    factoryOf(::RssStatusRepo)
    factoryOf(::RssStatusAdapter)
    factoryOf(::BlogAuthorAdapter)
    factoryOf(::RssPlatformTransformer)
    factoryOf(::RssSourceTransformer)
    factoryOf(::RssUriTransformer)
    factoryOf(::RssSourceWebFingerTransformer)
    factoryOf(::RssSourceViewModel)

    factoryOf(::RssStatusProvider) bind IStatusProvider::class
}

expect fun Module.createPlatformModule()
