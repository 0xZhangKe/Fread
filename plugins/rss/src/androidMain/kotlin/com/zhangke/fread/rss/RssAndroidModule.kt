package com.zhangke.fread.rss

import androidx.room.Room
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.RssParserBuilder
import com.zhangke.framework.architect.http.GlobalOkHttpClient
import com.zhangke.framework.utils.SystemUtils
import com.zhangke.fread.rss.internal.db.RssDatabases
import okhttp3.OkHttpClient
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
            callFactory = createRssOkHttpClient(
                appVersion = SystemUtils.getAppVersionName(androidContext()),
            ),
        ).build()
    }
}

private fun createRssOkHttpClient(appVersion: String): OkHttpClient {
    val userAgent = "Fread/$appVersion (Android) +https://fread.xyz/"
    return GlobalOkHttpClient.client.newBuilder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", userAgent)
                .build()
            chain.proceed(request)
        }
        .build()
}
