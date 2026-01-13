package com.zhangke.fread.common

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.di.ApplicationCoroutineScope
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val commonModule = module {
    createPlatformModule()
    factory<ApplicationCoroutineScope> { ApplicationScope }
    singleOf(::DayNightHelper)
}

expect fun Module.createPlatformModule()
