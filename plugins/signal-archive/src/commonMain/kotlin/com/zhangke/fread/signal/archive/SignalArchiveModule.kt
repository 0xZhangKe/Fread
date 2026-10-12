package com.zhangke.fread.signal.archive

import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.signal.archive.repo.SignalArchiveRepo
import com.zhangke.fread.signal.archive.screens.add.AddSignalArchiveContentViewModel
import com.zhangke.fread.signal.archive.screens.content.SignalArchiveContentViewModel
import com.zhangke.fread.signal.archive.screens.feeds.SignalArchiveFeedsViewModel
import com.zhangke.fread.signal.archive.screens.detail.SignalArchiveDetailViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val signalArchiveModule = module {

    single { SignalArchiveRepo() }
    viewModel { params -> SignalArchiveContentViewModel(params.get(), get()) }
    viewModel { params -> SignalArchiveFeedsViewModel(params.get(), get()) }
    viewModel { params -> SignalArchiveDetailViewModel(params.get(), get()) }
    viewModel { params ->
        AddSignalArchiveContentViewModel(
            archiveRepo = get(),
            contentRepo = get(),
            onboarding = get(),
            contentSwitcher = get(),
            initialUrl = params.get(),
        )
    }

    factoryOf(::SignalArchiveNavEntryProvider) bind NavEntryProvider::class

    factoryOf(::SignalArchiveContentManager)
    factoryOf(::SignalArchiveScreenProvider)
    factoryOf(::SignalArchiveSearchEngine)
    factoryOf(::SignalArchiveAccountManager)
    factoryOf(::SignalArchiveStatusResolver)
    factoryOf(::SignalArchiveStatusSourceResolver)
    factoryOf(::SignalArchiveNotificationResolver)
    factoryOf(::SignalArchivePublishManager)
    factoryOf(::SignalArchiveProvider) bind IStatusProvider::class

}
