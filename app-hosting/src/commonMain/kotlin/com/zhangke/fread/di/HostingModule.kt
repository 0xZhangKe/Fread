package com.zhangke.fread.di

import com.zhangke.fread.screen.main.MainViewModel
import com.zhangke.fread.screen.main.drawer.MainDrawerViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val hostingModule = module {
    createPlatformModule()
    viewModelOf(::MainViewModel)
    viewModelOf(::MainDrawerViewModel)
}

expect fun Module.createPlatformModule()
