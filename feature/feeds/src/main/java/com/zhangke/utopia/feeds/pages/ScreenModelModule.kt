package com.zhangke.utopia.feeds.pages

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.zhangke.utopia.feeds.pages.manager.add.pre.PreAddFeedsViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityComponent::class)
abstract class ScreenModelModule {

    @Binds
    @IntoMap
    @ScreenModelKey(PreAddFeedsViewModel::class)
    abstract fun bindPreAddFeedsViewModel(viewModel: PreAddFeedsViewModel): ScreenModel
}
