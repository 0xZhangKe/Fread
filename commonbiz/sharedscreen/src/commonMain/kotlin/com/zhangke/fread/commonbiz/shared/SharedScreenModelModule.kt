package com.zhangke.fread.commonbiz.shared

import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.commonbiz.shared.blog.detail.BlogDetailViewModel
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.MultiAccountPublishingViewModel
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextViewModel
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides

expect interface SharedScreenPlatformModule

interface SharedScreenModelModule : SharedScreenPlatformModule {

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

    @IntoMap
    @Provides
    fun provideMultiAccountPublishingViewModel(creator: (List<String>) -> MultiAccountPublishingViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return MultiAccountPublishingViewModel::class to MultiAccountPublishingViewModel.Factory {
            creator(it)
        }
    }

    @Provides
    @ApplicationScope
    fun provideModuleScreenVisitor(
        feedsScreenVisitor: IFeedsScreenVisitor,
        profileScreenVisitor: IProfileScreenVisitor,
    ): ModuleScreenVisitor {
        return ModuleScreenVisitor(
            feedsScreenVisitor = feedsScreenVisitor,
            profileScreenVisitor = profileScreenVisitor,
        )
    }
}
