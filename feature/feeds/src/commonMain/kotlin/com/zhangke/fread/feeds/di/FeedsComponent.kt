package com.zhangke.fread.feeds.di

import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.feeds.pages.home.ContentHomeViewModel
import com.zhangke.fread.feeds.pages.home.feeds.MixedContentViewModel
import com.zhangke.fread.feeds.pages.manager.add.mixed.AddMixedFeedsViewModel
import com.zhangke.fread.feeds.pages.manager.add.pre.PreAddFeedsViewModel
import com.zhangke.fread.feeds.pages.manager.edit.EditMixedContentViewModel
import com.zhangke.fread.feeds.pages.manager.importing.ImportFeedsViewModel
import com.zhangke.fread.feeds.pages.manager.search.SearchSourceForAddViewModel
import com.zhangke.fread.status.source.StatusSource
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides

interface FeedsComponent {

    @IntoMap
    @Provides
    fun provideContentHomeViewModel(creator: () -> ContentHomeViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return ContentHomeViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideMixedContentViewModel(creator: () -> MixedContentViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return MixedContentViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideAddMixedFeedsViewModel(creator: (StatusSource?) -> AddMixedFeedsViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return AddMixedFeedsViewModel::class to AddMixedFeedsViewModel.Factory { statusSource ->
            creator(statusSource)
        }
    }

    @IntoMap
    @Provides
    fun providePreAddFeedsViewModel(creator: () -> PreAddFeedsViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return PreAddFeedsViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideEditMixedContentViewModel(creator: (Long) -> EditMixedContentViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return EditMixedContentViewModel::class to EditMixedContentViewModel.Factory { configId ->
            creator(configId)
        }
    }

    @IntoMap
    @Provides
    fun provideImportFeedsViewModel(creator: () -> ImportFeedsViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return ImportFeedsViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideSearchSourceForAddViewModel(creator: () -> SearchSourceForAddViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return SearchSourceForAddViewModel::class to creator
    }
}