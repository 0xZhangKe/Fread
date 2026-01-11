package com.zhangke.fread.feature.message.di

import com.zhangke.fread.feature.message.repo.notification.NotificationsRepo
import com.zhangke.fread.feature.message.screens.home.NotificationsHomeViewModel
import com.zhangke.fread.feature.message.screens.notification.NotificationContainerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val notificationsModule = module {

    createPlatformModule()

    singleOf(::NotificationsRepo)

    viewModelOf(::NotificationsHomeViewModel)
    viewModelOf(::NotificationContainerViewModel)
}

expect fun Module.createPlatformModule()
