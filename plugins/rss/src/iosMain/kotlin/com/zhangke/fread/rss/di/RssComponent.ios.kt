package com.zhangke.fread.rss.di

import androidx.room.Room
import com.prof18.rssparser.RssParser
import com.prof18.rssparser.RssParserBuilder
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.documentDirectory
import com.zhangke.fread.rss.internal.db.RssDatabases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSURLSession

actual interface RssPlatformComponent {
    @ApplicationScope
    @Provides
    fun provideRssDatabases(): RssDatabases {
        val dbFilePath = documentDirectory() + "/${RssDatabases.DB_NAME}"
        return Room.databaseBuilder<RssDatabases>(
            name = dbFilePath,
        ).setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideRssParser(): RssParser {
        return RssParserBuilder(
            nsUrlSession = NSURLSession(),
        ).build()
    }
}
