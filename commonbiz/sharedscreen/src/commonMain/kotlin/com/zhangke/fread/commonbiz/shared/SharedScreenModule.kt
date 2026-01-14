package com.zhangke.fread.commonbiz.shared

import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.commonbiz.shared.blog.detail.RssBlogDetailViewModel
import com.zhangke.fread.commonbiz.shared.repo.SelectedAccountPublishingRepo
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.MultiAccountPublishingViewModel
import com.zhangke.fread.commonbiz.shared.screen.status.account.SelectAccountOpenStatusViewModel
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextViewModel
import com.zhangke.fread.commonbiz.shared.usecase.PublishPostOnMultiAccountUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val sharedScreenModule = module {

    createPlatformModule()

    factoryOf(::SharedScreenNavEntryProvider) bind NavEntryProvider::class

    factoryOf(::SelectedAccountPublishingRepo)
    factoryOf(::RefactorToNewBlogUseCase)
    factoryOf(::RefactorToNewStatusUseCase)
    factoryOf(::PublishPostOnMultiAccountUseCase)

    viewModelOf(::StatusContextViewModel)
    viewModelOf(::RssBlogDetailViewModel)
    viewModelOf(::MultiAccountPublishingViewModel)
    viewModelOf(::SelectAccountOpenStatusViewModel)

    singleOf(::ModuleScreenVisitor)
}

expect fun Module.createPlatformModule()
