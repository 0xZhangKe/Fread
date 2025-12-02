package com.zhangke.fread.common

import androidx.lifecycle.ViewModelProvider
import cafe.adriel.voyager.hilt.KotlinInjectViewModelProviderFactory
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.browser.BrowserLoadingViewModel
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.deeplink.SelectAccountForPublishViewModel
import com.zhangke.fread.common.di.ApplicationCoroutineScope
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.common.startup.FreadConfigModuleStartup
import com.zhangke.fread.common.startup.StartupManager
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

expect interface CommonPlatformComponent

interface CommonComponent : CommonPlatformComponent {

    val dayNightHelper: DayNightHelper

    val startupManager: StartupManager

    val statusProvider: StatusProvider

    val freadConfigManager: FreadConfigManager

    @IntoSet
    @Provides
    fun bindCommonStartup(module: CommonStartup): ModuleStartup {
        return module
    }

    @ApplicationScope
    @Provides
    fun provideApplicationCoroutineScope(): ApplicationCoroutineScope {
        return ApplicationScope
    }

    @ApplicationScope
    @Provides
    fun provideViewModelProviderFactory(
        viewModelMaps: Map<ViewModelKey, ViewModelCreator>,
        viewModelFactoryMaps: Map<ViewModelKey, ViewModelFactory>,
    ): ViewModelProvider.Factory {
        return KotlinInjectViewModelProviderFactory(
            viewModelMaps = viewModelMaps,
            viewModelFactoryMaps = viewModelFactoryMaps,
        )
    }

    @IntoSet
    @Provides
    fun bindFreadConfigStartup(module: FreadConfigModuleStartup): ModuleStartup {
        return module
    }

    @IntoMap
    @Provides
    fun provideBrowserLoadingViewModel(creator: (String, PlatformLocator?) -> BrowserLoadingViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return BrowserLoadingViewModel::class to BrowserLoadingViewModel.Factory { uri, locator ->
            creator(uri, locator)
        }
    }

    @IntoMap
    @Provides
    fun provideSelectAccountForPublishViewModel(creator: () -> SelectAccountForPublishViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return SelectAccountForPublishViewModel::class to creator
    }
}

interface CommonComponentProvider {
    val component: CommonComponent
}

lateinit var commonComponentProvider: CommonComponentProvider
