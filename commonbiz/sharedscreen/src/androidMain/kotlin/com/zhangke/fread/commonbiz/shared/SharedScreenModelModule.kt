package com.zhangke.fread.commonbiz.shared

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.hilt.ScreenModelKey
import com.zhangke.fread.commonbiz.shared.blog.detail.BlogDetailViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityComponent::class)
abstract class SharedScreenModelModule {

    @Binds
    @IntoMap
    @ScreenModelKey(BlogDetailViewModel::class)
    abstract fun bindPreAddFeedsViewModel(viewModel: BlogDetailViewModel): ScreenModel
}
