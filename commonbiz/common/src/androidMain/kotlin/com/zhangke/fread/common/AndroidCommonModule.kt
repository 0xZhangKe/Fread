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
import com.zhangke.fread.common.browser.AndroidSystemBrowserLauncher
import com.zhangke.fread.common.browser.OAuthHandler
import com.zhangke.fread.common.browser.SystemBrowserLauncher
import com.zhangke.fread.common.db.ContentConfigDatabases
import com.zhangke.fread.common.db.FreadContentDatabase
import com.zhangke.fread.common.db.MixedStatusDatabases
import com.zhangke.fread.common.db.old.OldFreadContentDatabase
import com.zhangke.fread.common.handler.TextHandler
import com.zhangke.fread.common.language.ActivityLanguageHelper
import com.zhangke.fread.common.language.LanguageHelper
import com.zhangke.fread.common.startup.LanguageModuleStartup
import com.zhangke.fread.common.utils.MediaFileHelper
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.common.utils.StorageHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

@OptIn(ExperimentalSettingsImplementation::class)
actual fun Module.createPlatformModule() {
    single<ContentConfigDatabases> {
        Room.databaseBuilder(
            androidContext(),
            ContentConfigDatabases::class.java,
            ContentConfigDatabases.DB_NAME,
        ).build()
    }
    single<FreadContentDatabase> {
        Room.databaseBuilder(
            androidContext(),
            FreadContentDatabase::class.java,
            FreadContentDatabase.DB_NAME,
        ).build()
    }
    single<OldFreadContentDatabase> {
        Room.databaseBuilder(
            androidContext(),
            OldFreadContentDatabase::class.java,
            OldFreadContentDatabase.DB_NAME,
        ).build()
    }
    single<MixedStatusDatabases> {
        Room.databaseBuilder(
            androidContext(),
            MixedStatusDatabases::class.java,
            MixedStatusDatabases.DB_NAME,
        ).addMigrations(MixedStatusDatabases.MIGRATION_1_2).build()
    }

    singleOf(::MediaFileHelper)
    singleOf(::PlatformUriHelper)
    singleOf(::StorageHelper)
    singleOf(::ActivityLanguageHelper)
    singleOf(::LanguageHelper)
    singleOf(::TextHandler)
    singleOf(::OAuthHandler)
    singleOf(::AndroidSystemBrowserLauncher) bind SystemBrowserLauncher::class
    factoryOf(::LanguageModuleStartup) bind ModuleStartup::class
    single<FlowSettings> {
        DataStoreSettings(androidContext().localConfig)
    } bind FlowSettings::class
}

val Context.localConfig: DataStore<Preferences> by preferencesDataStore(name = "local_config")
