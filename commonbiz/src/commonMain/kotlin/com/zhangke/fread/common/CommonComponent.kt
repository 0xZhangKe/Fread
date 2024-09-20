package com.zhangke.fread.common

import androidx.lifecycle.ViewModelProvider
import cafe.adriel.voyager.hilt.KotlinInjectViewModelProviderFactory
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

expect interface CommonPlatformComponent

interface CommonComponent : CommonPlatformComponent {

    val localConfigManager: LocalConfigManager

    val freadConfigManager: FreadConfigManager

    val viewModelProviderFactory: ViewModelProvider.Factory

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
    fun bindFreadConfigStartup(module: FreadConfigStartup): ModuleStartup {
        return module
    }
}

interface CommonComponentProvider {
    val component: CommonComponent
}
