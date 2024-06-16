package com.zhangke.fread

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.zhangke.fread.feeds.pages.manager.importing.ImportFeedsViewModel
import com.zhangke.fread.screen.main.drawer.MainDrawerScreenModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityComponent::class)
abstract class AppModule {

    @Binds
    @IntoMap
    @ScreenModelKey(MainDrawerScreenModel::class)
    abstract fun bindMainDrawerScreenModel(viewModel: MainDrawerScreenModel): ScreenModel
}
