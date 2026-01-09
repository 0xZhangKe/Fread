package com.zhangke.fread.di

import com.zhangke.framework.utils.appContext
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication

actual class PlatformedMemApplication {

    actual fun KoinApplication.initKoin() {
        androidLogger()
        androidContext(appContext)
    }
}
