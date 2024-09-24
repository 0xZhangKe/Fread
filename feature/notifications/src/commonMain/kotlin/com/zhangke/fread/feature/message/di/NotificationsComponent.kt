package com.zhangke.fread.feature.message.di

import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.feature.message.screens.home.NotificationsHomeViewModel
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides

interface NotificationsComponent {
    @IntoMap
    @Provides
    fun provideNotificationsHomeViewModel(creator: () -> NotificationsHomeViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return NotificationsHomeViewModel::class to creator
    }
}