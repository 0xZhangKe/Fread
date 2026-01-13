package com.zhangke.fread.common

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.common.account.ActiveAccountsSynchronizer
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.bubble.BubbleManager
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.browser.UrlRedirectViewModel
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.LocalConfigManager
import com.zhangke.fread.common.content.FreadContentDbMigrateManager
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.daynight.DayNightHelper
import com.zhangke.fread.common.deeplink.SelectedContentSwitcher
import com.zhangke.fread.common.deeplink.SelectAccountForPublishViewModel
import com.zhangke.fread.common.di.ApplicationCoroutineScope
import com.zhangke.fread.common.mixed.MixedStatusRepo
import com.zhangke.fread.common.onboarding.OnboardingComponent
import com.zhangke.fread.common.publish.PublishPostManager
import com.zhangke.fread.common.review.FreadReviewManager
import com.zhangke.fread.common.startup.FreadConfigModuleStartup
import com.zhangke.fread.common.startup.FeedsRepoModuleStartup
import com.zhangke.fread.common.startup.StartupManager
import com.zhangke.fread.common.status.StatusIdGenerator
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.adapter.ContentConfigAdapter
import com.zhangke.fread.common.status.usecase.FormatStatusDisplayTimeUseCase
import com.zhangke.fread.common.update.AppUpdateManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val commonModule = module {
    createPlatformModule()
    factory<ApplicationCoroutineScope> { ApplicationScope }
    singleOf(::ActiveAccountsSynchronizer)
    singleOf(::BubbleManager)
    singleOf(::DayNightHelper)
    singleOf(::FreadConfigManager)
    singleOf(::FreadReviewManager)
    singleOf(::LocalConfigManager)
    singleOf(::OnboardingComponent)
    singleOf(::PublishPostManager)
    singleOf(::SelectedContentSwitcher)
    singleOf(::StartupManager)
    singleOf(::StatusUpdater)

    factoryOf(::AppUpdateManager)
    factoryOf(::BrowserLauncher)
    factoryOf(::ContentConfigAdapter)
    factoryOf(::FreadContentDbMigrateManager)
    factoryOf(::FreadContentRepo)
    factoryOf(::FormatStatusDisplayTimeUseCase)
    factoryOf(::MixedStatusRepo)
    factoryOf(::StatusIdGenerator)
    factoryOf(::StatusUiStateAdapter)

    factoryOf(::CommonStartup) bind ModuleStartup::class
    factoryOf(::FreadConfigModuleStartup) bind ModuleStartup::class
    factoryOf(::FeedsRepoModuleStartup) bind ModuleStartup::class

    viewModelOf(::SelectAccountForPublishViewModel)
    viewModelOf(::UrlRedirectViewModel)
}

expect fun Module.createPlatformModule()
