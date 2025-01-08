package com.zhangke.fread.di

import com.seiko.imageloader.ImageLoader
import com.zhangke.fread.activitypub.app.di.ActivityPubComponent
import com.zhangke.fread.common.CommonComponent
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.commonbiz.shared.SharedScreenModelModule
import com.zhangke.fread.explore.di.ExploreComponent
import com.zhangke.fread.feature.message.di.NotificationsComponent
import com.zhangke.fread.feeds.di.FeedsComponent
import com.zhangke.fread.profile.di.ProfileComponent
import com.zhangke.fread.rss.di.RssComponent
import com.zhangke.fread.screen.main.MainViewModel
import com.zhangke.fread.screen.main.drawer.MainDrawerViewModel
import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.status.StatusProvider
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides
import kotlin.jvm.JvmSuppressWildcards

interface HostingApplicationComponent : CommonComponent,
    SharedScreenModelModule,
    ExploreComponent,
    FeedsComponent,
    NotificationsComponent,
    ProfileComponent,
    ActivityPubComponent,
    RssComponent {

    val imageLoader: ImageLoader

    @Provides
    @ApplicationScope
    fun provideProvideStatusProvider(
        providers: Set<@JvmSuppressWildcards IStatusProvider>,
    ): StatusProvider {
        return StatusProvider(providers)
    }

    @IntoMap
    @Provides
    fun provideMainDrawerMainDrawerViewModel(creator: () -> MainDrawerViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return MainDrawerViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideMainPageViewModel(creator: () -> MainViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return MainViewModel::class to creator
    }
}
