package com.zhangke.fread.bluesky

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentViewModel
import com.zhangke.fread.bluesky.internal.screen.home.BlueskyHomeContainerViewModel
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.status.IStatusProvider
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

expect interface BlueskyPlatformComponent

interface BlueskyComponent : BlueskyPlatformComponent {

    @IntoSet
    @Provides
    fun provideActivityPubProvider(blueskyProvider: BlueskyProvider): IStatusProvider {
        return blueskyProvider
    }

    @IntoMap
    @Provides
    fun provideAddBlueskyContentViewModel(creator: (FormalBaseUrl) -> AddBlueskyContentViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return AddBlueskyContentViewModel::class to AddBlueskyContentViewModel.Factory { baseUrl ->
            creator(baseUrl)
        }
    }

    @IntoMap
    @Provides
    fun provideBlueskyHomeContainerViewModel(creator: () -> BlueskyHomeContainerViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return BlueskyHomeContainerViewModel::class to creator
    }
}
