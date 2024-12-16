package com.zhangke.fread.common

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.db.ContentConfigDatabases
import com.zhangke.fread.common.db.FreadContentDatabase
import com.zhangke.fread.common.db.StatusDatabase
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.review.FreadReviewManager
import com.zhangke.fread.common.startup.FeedsRepoModuleStartup
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

private val Context.localConfig: DataStore<Preferences> by preferencesDataStore(name = "local_config")

actual interface CommonPlatformComponent {

    val moduleStartups: Set<ModuleStartup>

    val browserInterceptorSet: Set<BrowserInterceptor>

    val dayNightHelper: DayNightHelper

    val freadReviewManager: FreadReviewManager

    val browserLauncher: BrowserLauncher

    @OptIn(ExperimentalSettingsImplementation::class)
    @ApplicationScope
    @Provides
    fun provideFlowSettings(
        context: ApplicationContext,
    ): FlowSettings {
        return DataStoreSettings(context.localConfig)
    }

    @ApplicationScope
    @Provides
    fun provideStatusDatabases(
        context: ApplicationContext,
    ): StatusDatabase {
        return Room.databaseBuilder(
            context,
            StatusDatabase::class.java,
            StatusDatabase.DB_NAME,
        ).addMigrations(
            StatusDatabase.Status1to2Migration(),
        ).build()
    }

    @ApplicationScope
    @Provides
    fun provideContentConfigDatabases(
        context: ApplicationContext,
    ): ContentConfigDatabases {
        return Room.databaseBuilder(
            context,
            ContentConfigDatabases::class.java,
            ContentConfigDatabases.DB_NAME,
        ).build()
    }

    @ApplicationScope
    @Provides
    fun provideFreadContentDatabases(
        context: ApplicationContext,
    ): FreadContentDatabase {
        return Room.databaseBuilder(
            context,
            FreadContentDatabase::class.java,
            FreadContentDatabase.DB_NAME,
        ).build()
    }

    @IntoSet
    @Provides
    fun bindCommonBizModuleStartup(module: FeedsRepoModuleStartup): ModuleStartup {
        return module
    }

    @IntoSet
    @Provides
    fun bindCommonModuleStartup(module: CommonModuleStartup): ModuleStartup {
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

val Context.commonComponent get() = (applicationContext as CommonComponentProvider).component
