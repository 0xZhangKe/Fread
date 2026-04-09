package com.zhangke.fread.rss

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.RssParserBuilder
import com.zhangke.fread.common.documentDirectory
import com.zhangke.fread.rss.internal.db.RssDatabases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import platform.Foundation.NSBundle
import platform.Foundation.NSMutableDictionary
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSession

actual fun Module.createPlatformModule() {
    single<RssDatabases> {
        val dbFilePath = getDBFilePath(RssDatabases.DB_NAME)
        Room.databaseBuilder<RssDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<RssParser> {
        RssParserBuilder(
            nsUrlSession = createRssUrlSession(
                appVersion = NSBundle.mainBundle()
                    .infoDictionary()
                    ?.get("CFBundleShortVersionString") as? String ?: "",
            ),
        ).build()
    }
}

private fun getDBFilePath(dbName: String): String {
    return documentDirectory() + "/$dbName"
}

private fun createRssUrlSession(appVersion: String): NSURLSession {
    val configuration = NSURLSessionConfiguration.defaultSessionConfiguration()
    configuration.HTTPAdditionalHeaders = buildMap {
        put("User-Agent", "Fread/$appVersion (iOS) +https://fread.xyz/")
    }
    return NSURLSession.sessionWithConfiguration(configuration)
}
