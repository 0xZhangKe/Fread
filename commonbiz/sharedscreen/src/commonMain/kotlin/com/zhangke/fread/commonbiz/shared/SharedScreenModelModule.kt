package com.zhangke.fread.commonbiz.shared

import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.commonbiz.shared.blog.detail.BlogDetailViewModel
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextViewModel
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides

interface SharedScreenModelModule {

    @IntoMap
    @Provides
    fun provideStatusContextViewModel(creator: () -> StatusContextViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return StatusContextViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideBlogDetailViewModel(creator: () -> BlogDetailViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return BlogDetailViewModel::class to creator
    }

    @Provides
    @ApplicationScope
    fun provideModuleScreenVisitor(
        feedsScreenVisitor: IFeedsScreenVisitor,
    ): ModuleScreenVisitor {
        return ModuleScreenVisitor(
            feedsScreenVisitor = feedsScreenVisitor,
        )
    }
}
