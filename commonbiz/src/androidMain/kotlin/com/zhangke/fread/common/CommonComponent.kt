package com.zhangke.fread.common

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.ViewModelInitializer
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.browser.BrowserInterceptor
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

    val viewModelMaps: Map<ViewModelKey, ViewModelCreator>

    val viewModelFactoryMaps: Map<ViewModelKey, ViewModelFactory>

    @ApplicationScope
    @Provides
    fun provideStatusDatabases(
        application: Application,
        // @ApplicationContext context: Context
    ): StatusDatabase {
        return StatusDatabase.getInstance(application)
    }

    @Provides
    fun provideContentConfigDatabases(
        // @ApplicationContext context: Context
        application: Application,
    ): ContentConfigDatabases {
        return ContentConfigDatabases.getInstance(application)
    }

    @IntoSet
    @Provides
    fun bindCommonBizModuleStartup(module: CommonBizModuleStartup): ModuleStartup {
        return module
    }
}

interface CommonComponentProvider {
    val component: CommonComponent
}

val Context.commonComponent get() = (applicationContext as CommonComponentProvider).component
