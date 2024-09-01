package com.zhangke.fread.rss.di

import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.rss.RssStatusProvider
import com.zhangke.fread.rss.internal.db.RssDatabases
import com.zhangke.fread.rss.internal.screen.source.RssSourceViewModel
import com.zhangke.fread.status.IStatusProvider
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface RssComponent {

    @Provides
    fun provideRssDatabases(context: ApplicationContext): RssDatabases {
        return RssDatabases.getInstance(context)
    }

    @IntoSet
    @Provides
    fun provideIStatusProvider(rssStatusProvider: RssStatusProvider): IStatusProvider {
        return rssStatusProvider
    }

    @IntoMap
    @Provides
    fun provideRssSourceViewModel(creator: (String) -> RssSourceViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return RssSourceViewModel::class to RssSourceViewModel.Factory { url ->
            creator(url)
        }
    }
}