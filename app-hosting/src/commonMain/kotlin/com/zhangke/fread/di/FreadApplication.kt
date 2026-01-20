package com.zhangke.fread.di

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.activitypub.app.di.activityPubModule
import com.zhangke.fread.bluesky.blueskyModule
import com.zhangke.fread.common.commonModule
import com.zhangke.fread.commonbiz.shared.sharedScreenModule
import com.zhangke.fread.explore.di.exploreModule
import com.zhangke.fread.feature.message.di.notificationsModule
import com.zhangke.fread.feeds.di.feedsModule
import com.zhangke.fread.profile.di.profileModule
import com.zhangke.fread.rss.rssModule
import com.zhangke.fread.status.statusProviderModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

object FreadApplication {

    fun initialize() {
        val koin = startKoin {
            PlatformedFreadApplication().apply {
                initKoin()
            }
            modules(
                hostingModule,
                commonModule,
                profileModule,
                activityPubModule,
                statusProviderModel,
                blueskyModule,
                rssModule,
                profileModule,
                exploreModule,
                notificationsModule,
                feedsModule,
                sharedScreenModule,
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            koin.koin.getAll<ModuleStartup>().forEach { it.onAppCreate() }
        }
    }
}

expect class PlatformedFreadApplication() {

    fun KoinApplication.initKoin()
}
