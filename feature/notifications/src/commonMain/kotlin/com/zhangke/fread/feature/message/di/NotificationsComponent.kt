package com.zhangke.fread.feature.message.di

import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.feature.message.screens.home.NotificationsHomeViewModel
import com.zhangke.fread.feature.message.screens.notification.NotificationContainerViewModel
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides

expect interface NotificationsComponentPlatform

interface NotificationsComponent: NotificationsComponentPlatform {

    @IntoMap
    @Provides
    fun provideNotificationsHomeViewModel(creator: () -> NotificationsHomeViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return NotificationsHomeViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideNotificationContainerViewModel(creator: () -> NotificationContainerViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return NotificationContainerViewModel::class to creator
    }
}
