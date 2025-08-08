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
import com.zhangke.fread.common.db.ContentConfigDatabases
import com.zhangke.fread.common.db.FreadContentDatabase
import com.zhangke.fread.common.db.old.OldFreadContentDatabase
import com.zhangke.fread.common.db.MixedStatusDatabases
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.startup.FeedsRepoModuleStartup
import com.zhangke.fread.common.startup.LanguageModuleStartup
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

    @ApplicationScope
    @Provides
    fun provideOldFreadContentDatabases(
        context: ApplicationContext,
    ): OldFreadContentDatabase {
        return Room.databaseBuilder(
            context,
            OldFreadContentDatabase::class.java,
            OldFreadContentDatabase.DB_NAME,
        ).build()
    }

    @ApplicationScope
    @Provides
    fun provideMixedStatusDatabases(context: ApplicationContext): MixedStatusDatabases {
        return Room.databaseBuilder(
            context,
            MixedStatusDatabases::class.java,
            MixedStatusDatabases.DB_NAME,
        ).addMigrations(MixedStatusDatabases.MIGRATION_1_2).build()
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
