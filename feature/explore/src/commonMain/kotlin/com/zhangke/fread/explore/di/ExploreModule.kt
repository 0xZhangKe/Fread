package com.zhangke.fread.explore.di

import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.explore.ExploreNavEntryProvider
import com.zhangke.fread.explore.screens.home.ExplorerHomeViewModel
import com.zhangke.fread.explore.screens.search.SearchViewModel
import com.zhangke.fread.explore.screens.search.author.SearchAuthorViewModel
import com.zhangke.fread.explore.screens.search.bar.SearchBarViewModel
import com.zhangke.fread.explore.screens.search.hashtag.SearchHashtagViewModel
import com.zhangke.fread.explore.screens.search.platform.SearchPlatformViewModel
import com.zhangke.fread.explore.screens.search.status.SearchStatusViewModel
import com.zhangke.fread.explore.usecase.BuildSearchResultUiStateUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val exploreModule = module {

    factoryOf(::ExploreNavEntryProvider) bind NavEntryProvider::class

    factoryOf(::BuildSearchResultUiStateUseCase)

    viewModelOf(::ExplorerHomeViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SearchBarViewModel)
    viewModelOf(::SearchAuthorViewModel)
    viewModelOf(::SearchHashtagViewModel)
    viewModelOf(::SearchPlatformViewModel)
    viewModelOf(::SearchStatusViewModel)
}
