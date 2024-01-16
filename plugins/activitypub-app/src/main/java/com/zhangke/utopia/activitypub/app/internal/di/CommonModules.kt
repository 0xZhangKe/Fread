package com.zhangke.utopia.activitypub.app.internal.di

import android.content.Context
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.db.lists.AccountListsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class CommonModules {

    @Provides
    fun provideActivityPubDatabases(@ApplicationContext context: Context): ActivityPubDatabases {
        return ActivityPubDatabases.getInstance(context)
    }

    @Provides
    fun provideAccountListsDatabase(@ApplicationContext context: Context): AccountListsDatabase {
        return AccountListsDatabase.getInstance(context)
    }
}
