package com.zhangke.fread.feeds.di

import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.commonbiz.shared.IFeedsScreenVisitor
import com.zhangke.fread.feeds.FeedsNavEntryProvider
import com.zhangke.fread.feeds.FeedsScreenVisitor
import com.zhangke.fread.feeds.pages.home.ContentHomeViewModel
import com.zhangke.fread.feeds.pages.home.feeds.MixedContentViewModel
import com.zhangke.fread.feeds.pages.manager.add.mixed.AddMixedFeedsViewModel
import com.zhangke.fread.feeds.pages.manager.add.type.SelectContentTypeViewModel
import com.zhangke.fread.feeds.pages.manager.edit.EditMixedContentViewModel
import com.zhangke.fread.feeds.pages.manager.importing.ImportFeedsViewModel
import com.zhangke.fread.feeds.pages.manager.search.SearchSourceForAddViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val feedsModule = module {

    factoryOf(::FeedsNavEntryProvider) bind NavEntryProvider::class

    viewModelOf(::ContentHomeViewModel)
    viewModelOf(::MixedContentViewModel)
    viewModelOf(::AddMixedFeedsViewModel)
    viewModelOf(::EditMixedContentViewModel)
    viewModelOf(::ImportFeedsViewModel)
    viewModelOf(::SearchSourceForAddViewModel)
    viewModelOf(::SelectContentTypeViewModel)

    singleOf(::FeedsScreenVisitor) bind IFeedsScreenVisitor::class
}
