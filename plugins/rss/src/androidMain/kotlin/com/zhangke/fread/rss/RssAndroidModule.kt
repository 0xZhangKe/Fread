package com.zhangke.fread.rss

import androidx.room.Room
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.RssParserBuilder
import com.zhangke.framework.architect.http.GlobalOkHttpClient
import com.zhangke.fread.rss.internal.db.RssDatabases
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

actual fun Module.createPlatformModule() {
    single<RssDatabases> {
        Room.databaseBuilder(
            androidContext(),
            RssDatabases::class.java,
            RssDatabases.DB_NAME,
        ).build()
    }
    single<RssParser> {
        RssParserBuilder(
            callFactory = GlobalOkHttpClient.client,
        ).build()
    }
}
