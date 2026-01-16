package com.zhangke.fread.common

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.zhangke.fread.common.browser.IosSystemBrowserLauncher
import com.zhangke.fread.common.browser.SystemBrowserLauncher
import com.zhangke.fread.common.db.ContentConfigDatabases
import com.zhangke.fread.common.db.FreadContentDatabase
import com.zhangke.fread.common.db.MixedStatusDatabases
import com.zhangke.fread.common.db.old.OldFreadContentDatabase
import com.zhangke.fread.common.handler.TextHandler
import com.zhangke.fread.common.language.ActivityLanguageHelper
import com.zhangke.fread.common.utils.MediaFileHelper
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.common.utils.StorageHelper
import com.zhangke.fread.common.utils.ToastHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import platform.Foundation.NSUserDefaults

actual fun Module.createPlatformModule() {
    single<ContentConfigDatabases> {
        val dbFilePath = getDBFilePath(ContentConfigDatabases.DB_NAME)
        Room.databaseBuilder<ContentConfigDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<FreadContentDatabase> {
        val dbFilePath = getDBFilePath(FreadContentDatabase.DB_NAME)
        Room.databaseBuilder<FreadContentDatabase>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<OldFreadContentDatabase> {
        val dbFilePath = getDBFilePath(OldFreadContentDatabase.DB_NAME)
        Room.databaseBuilder<OldFreadContentDatabase>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<MixedStatusDatabases> {
        val dbFilePath = getDBFilePath(MixedStatusDatabases.DB_NAME)
        Room.databaseBuilder<MixedStatusDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    singleOf(::MediaFileHelper)
    singleOf(::PlatformUriHelper)
    singleOf(::StorageHelper)
    singleOf(::ToastHelper)
    singleOf(::ActivityLanguageHelper)
    singleOf(::TextHandler)
    singleOf(::IosSystemBrowserLauncher) bind SystemBrowserLauncher::class
    single<FlowSettings> {
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults).toFlowSettings()
    }.bind(FlowSettings::class)
}

private fun getDBFilePath(dbName: String): String {
    return documentDirectory() + "/$dbName"
}
