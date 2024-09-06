package com.zhangke.fread.common

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import cafe.adriel.voyager.hilt.KotlinInjectViewModelProviderFactory
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.common.status.repo.db.ContentConfigDatabases
import com.zhangke.fread.common.status.repo.db.StatusDatabase
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface CommonComponent {

    val moduleStartups: Set<ModuleStartup>

    val browserInterceptorSet: Set<BrowserInterceptor>

    val viewModelProviderFactory: ViewModelProvider.Factory

    val browserLauncher: BrowserLauncher

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

    @ApplicationScope
    @Provides
    fun provideStatusDatabases(
        context: ApplicationContext,
    ): StatusDatabase {
        return StatusDatabase.getInstance(context)
    }

    @Provides
    fun provideContentConfigDatabases(
        context: ApplicationContext,
    ): ContentConfigDatabases {
        return ContentConfigDatabases.getInstance(context)
    }

    @IntoSet
    @Provides
    fun bindCommonBizModuleStartup(module: CommonBizModuleStartup): ModuleStartup {
        return module
    }

    @ApplicationScope
    @Provides
    fun provideBrowserLauncher(
        context: ApplicationContext,
    ): BrowserLauncher {
        return BrowserLauncher(context)
    }
}

interface CommonComponentProvider {
    val component: CommonComponent
}

val Context.commonComponent get() = (applicationContext as CommonComponentProvider).component
