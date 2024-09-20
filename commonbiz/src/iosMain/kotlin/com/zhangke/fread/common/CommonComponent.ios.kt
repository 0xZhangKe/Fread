package com.zhangke.fread.common

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSUserDefaults

actual interface CommonPlatformComponent {

    @ApplicationScope
    @Provides
    fun provideFlowSettings(delegate: NSUserDefaults): FlowSettings {
        return NSUserDefaultsSettings(delegate).toFlowSettings()
    }
}