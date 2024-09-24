package com.zhangke.fread.rss.di

import androidx.room.Room
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.RssParserBuilder
import com.zhangke.framework.architect.http.GlobalOkHttpClient
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.rss.internal.db.RssDatabases
import me.tatarka.inject.annotations.Provides

actual interface RssPlatformComponent {

    @ApplicationScope
    @Provides
    fun provideRssDatabases(context: ApplicationContext): RssDatabases {
        return Room.databaseBuilder(
            context,
            RssDatabases::class.java,
            RssDatabases.DB_NAME,
        ).build()
    }

    @ApplicationScope
    @Provides
    fun provideRssParser(): RssParser {
        return RssParserBuilder(
            callFactory = GlobalOkHttpClient.client,
        ).build()
    }
}