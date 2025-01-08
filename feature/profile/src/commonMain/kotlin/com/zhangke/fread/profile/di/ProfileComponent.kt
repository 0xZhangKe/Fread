package com.zhangke.fread.profile.di

import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.profile.screen.home.ProfileHomeViewModel
import com.zhangke.fread.profile.screen.setting.SettingScreenModel
import com.zhangke.fread.profile.screen.setting.about.AboutViewModel
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides

interface ProfileComponent {

    @IntoMap
    @Provides
    fun provideProfileHomeViewModel(creator: () -> ProfileHomeViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return ProfileHomeViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideSettingScreenModel(creator: () -> SettingScreenModel): Pair<ViewModelKey, ViewModelCreator> {
        return SettingScreenModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideAboutViewModel(creator: () -> AboutViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return AboutViewModel::class to creator
    }
}
