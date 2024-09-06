package com.zhangke.fread.di

import android.app.Application
import android.content.Context
import com.zhangke.fread.activitypub.app.di.ActivityPubComponent
import com.zhangke.fread.activitypub.app.di.ActivityPubComponentProvider
import com.zhangke.fread.common.CommonComponent
import com.zhangke.fread.common.CommonComponentProvider
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.commonbiz.shared.SharedScreenModelModule
import com.zhangke.fread.explore.di.ExploreComponent
import com.zhangke.fread.feature.message.di.NotificationsComponent
import com.zhangke.fread.feeds.di.FeedsComponent
import com.zhangke.fread.profile.di.ProfileComponent
import com.zhangke.fread.rss.di.RssComponent
import com.zhangke.fread.screen.main.drawer.MainDrawerViewModel
import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.status.StatusProvider
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides
import javax.inject.Singleton

@Component
@ApplicationScope
abstract class ApplicationComponent(
    @get:Provides val application: Application,
) : CommonComponent,
    SharedScreenModelModule,
    ExploreComponent,
    FeedsComponent,
    NotificationsComponent,
    ProfileComponent,
    ActivityPubComponent,
    RssComponent {

    @Provides
    fun provideApplicationContext(): ApplicationContext {
        return application
    }

    @Provides
    @Singleton
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

    companion object
}

interface ApplicationComponentProvider : CommonComponentProvider, ActivityPubComponentProvider {
    override val component: ApplicationComponent
}

val Context.applicationComponent get() = (applicationContext as ApplicationComponentProvider).component
