package com.zhangke.fread.di

import com.zhangke.framework.module.ModuleStartup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

object FreadApplication {

    fun initialize() {
        val koin = startKoin {
            PlatformedMemApplication().apply {
                initKoin()
            }
            modules(
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            koin.koin.getAll<ModuleStartup>().forEach { it.onAppCreate() }
        }
    }
}

expect class PlatformedMemApplication() {

    fun KoinApplication.initKoin()
}
