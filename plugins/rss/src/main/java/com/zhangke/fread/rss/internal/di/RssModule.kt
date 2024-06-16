package com.zhangke.fread.rss.internal.di

import android.content.Context
import com.zhangke.fread.rss.internal.db.RssDatabases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class RssModule {

    @Provides
    fun provideRssDatabases(@ApplicationContext context: Context): RssDatabases {
        return RssDatabases.getInstance(context)
    }
}
