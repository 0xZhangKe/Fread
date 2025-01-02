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
import com.zhangke.fread.common.browser.OAuthHandler
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.startup.FeedsRepoModuleStartup
import com.zhangke.fread.common.startup.LanguageModuleStartup
import com.zhangke.fread.common.status.repo.db.ContentConfigDatabases
import com.zhangke.fread.common.status.repo.db.StatusDatabase
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

private val Context.localConfig: DataStore<Preferences> by preferencesDataStore(name = "local_config")

actual interface CommonPlatformComponent {

    val browserInterceptorSet: Set<BrowserInterceptor>

    val oauthHandler: OAuthHandler

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

    @IntoSet
    @Provides
    fun bindFeedsRepoModuleStartup(module: FeedsRepoModuleStartup): ModuleStartup {
        return module
    }

    @IntoSet
    @Provides
    fun bindLanguageModuleStartup(module: LanguageModuleStartup): ModuleStartup {
        return module
    }
}

val Context.commonComponent get() = (applicationContext as CommonComponentProvider).component
