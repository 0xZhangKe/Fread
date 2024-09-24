package com.zhangke.fread.commonbiz.shared

import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.commonbiz.shared.blog.detail.BlogDetailViewModel
import com.zhangke.fread.commonbiz.shared.screen.login.LoginViewModel
import com.zhangke.fread.commonbiz.shared.screen.login.target.LoginToTargetPlatformViewModel
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextViewModel
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides

interface SharedScreenModelModule {

    @IntoMap
    @Provides
    fun provideLoginViewModel(creator: () -> LoginViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return LoginViewModel::class to creator
    }

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
    fun provideLoginToTargetPlatformViewModel(creator: (BlogPlatform) -> LoginToTargetPlatformViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return LoginToTargetPlatformViewModel::class to LoginToTargetPlatformViewModel.Factory { platform ->
            creator(platform)
        }
    }
}
