package com.zhangke.fread.explore.di

import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.explore.screens.home.ExplorerHomeViewModel
import com.zhangke.fread.explore.screens.search.SearchViewModel
import com.zhangke.fread.explore.screens.search.author.SearchAuthorViewModel
import com.zhangke.fread.explore.screens.search.bar.SearchBarViewModel
import com.zhangke.fread.explore.screens.search.hashtag.SearchHashtagViewModel
import com.zhangke.fread.explore.screens.search.platform.SearchPlatformViewModel
import com.zhangke.fread.explore.screens.search.status.SearchStatusViewModel
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.Provides

interface ExploreComponent {

    @IntoMap
    @Provides
    fun provideExplorerHomeViewModel(creator: () -> ExplorerHomeViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return ExplorerHomeViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideSearchAuthorViewModel(creator: (IdentityRole) -> SearchAuthorViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return SearchAuthorViewModel::class to SearchAuthorViewModel.Factory { role ->
            creator(role)
        }
    }

    @IntoMap
    @Provides
    fun provideSearchBarViewModel(creator: () -> SearchBarViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return SearchBarViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideSearchHashtagViewModel(creator: (IdentityRole) -> SearchHashtagViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return SearchHashtagViewModel::class to SearchHashtagViewModel.Factory { role ->
            creator(role)
        }
    }

    @IntoMap
    @Provides
    fun provideSearchPlatformViewModel(creator: (IdentityRole, String) -> SearchPlatformViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return SearchPlatformViewModel::class to SearchPlatformViewModel.Factory { role, query ->
            creator(role, query)
        }
    }

    @IntoMap
    @Provides
    fun provideSearchStatusViewModel(creator: (IdentityRole) -> SearchStatusViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return SearchStatusViewModel::class to SearchStatusViewModel.Factory { role ->
            creator(role)
        }
    }

    @IntoMap
    @Provides
    fun provideSearchViewModel(creator: () -> SearchViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return SearchViewModel::class to creator
    }
}