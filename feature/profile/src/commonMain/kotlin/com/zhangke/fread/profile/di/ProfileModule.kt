package com.zhangke.fread.profile.di

import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.commonbiz.shared.IProfileScreenVisitor
import com.zhangke.fread.profile.ProfileNavEntryProvider
import com.zhangke.fread.profile.ProfileScreenVisitor
import com.zhangke.fread.profile.screen.home.ProfileHomeViewModel
import com.zhangke.fread.profile.screen.setting.SettingScreenModel
import com.zhangke.fread.profile.screen.setting.about.AboutViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val profileModule = module {

    factoryOf(::ProfileNavEntryProvider) bind NavEntryProvider::class

    viewModelOf(::ProfileHomeViewModel)
    viewModelOf(::SettingScreenModel)
    viewModelOf(::AboutViewModel)

    singleOf(::ProfileScreenVisitor) bind IProfileScreenVisitor::class
}
