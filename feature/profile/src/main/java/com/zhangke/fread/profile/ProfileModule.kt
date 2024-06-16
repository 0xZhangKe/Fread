package com.zhangke.fread.profile

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.zhangke.fread.profile.screen.setting.SettingScreenModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityComponent::class)
abstract class ProfileModule {

    @Binds
    @IntoMap
    @ScreenModelKey(SettingScreenModel::class)
    abstract fun bindSettingScreenModel(viewModel: SettingScreenModel): ScreenModel

}
