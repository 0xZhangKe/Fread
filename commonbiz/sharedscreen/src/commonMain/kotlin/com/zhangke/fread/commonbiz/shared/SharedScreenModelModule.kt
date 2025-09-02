package com.zhangke.fread.commonbiz.shared

import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.commonbiz.shared.blog.detail.RssBlogDetailViewModel
import com.zhangke.fread.commonbiz.shared.screen.image.GenerateImageAltViewModel
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.MultiAccountPublishingViewModel
import com.zhangke.fread.commonbiz.shared.screen.status.account.SelectAccountOpenStatusViewModel
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextViewModel
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusProviderProtocol
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
    fun provideBlogDetailViewModel(creator: () -> RssBlogDetailViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return RssBlogDetailViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideMultiAccountPublishingViewModel(creator: (List<String>) -> MultiAccountPublishingViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return MultiAccountPublishingViewModel::class to MultiAccountPublishingViewModel.Factory {
            creator(it)
        }
    }

    @IntoMap
    @Provides
    fun provideGenerateImageAltViewModel(creator: (String) -> GenerateImageAltViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return GenerateImageAltViewModel::class to GenerateImageAltViewModel.Factory {
            creator(it)
        }
    }

    @IntoMap
    @Provides
    fun provideSelectAccountOpenStatusViewModel(
        creator: (String, String, PlatformLocator, StatusProviderProtocol) -> SelectAccountOpenStatusViewModel,
    ): Pair<ViewModelKey, ViewModelFactory> {
        return SelectAccountOpenStatusViewModel::class to SelectAccountOpenStatusViewModel.Factory { blogId, blogUrl, locator, protocol ->
            creator(blogId, blogUrl, locator, protocol)
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
