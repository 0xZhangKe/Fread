package com.zhangke.fread.commonbiz.shared

import androidx.room.Room
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.commonbiz.shared.db.SelectedAccountPublishingDatabase
import me.tatarka.inject.annotations.Provides

actual interface SharedScreenPlatformModule {

    @ApplicationScope
    @Provides
    fun provideSelectedAccountPublishingDatabase(context: ApplicationContext): SelectedAccountPublishingDatabase {
        return Room.databaseBuilder(
            context,
            SelectedAccountPublishingDatabase::class.java,
            SelectedAccountPublishingDatabase.DB_NAME,
        ).build()
    }
}
