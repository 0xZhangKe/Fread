package com.zhangke.utopia.feeds

import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import com.zhangke.utopia.feeds.pages.home.feeds.FeedsViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityComponent::class)
abstract class FeedsInActivityHiltModule {

    @Binds
    @IntoMap
    @ScreenModelFactoryKey(FeedsViewModel.Factory::class)
    abstract fun bindHiltDetailsScreenModelFactory(
        feedsViewModelFactory: FeedsViewModel.Factory
    ): ScreenModelFactory
}
