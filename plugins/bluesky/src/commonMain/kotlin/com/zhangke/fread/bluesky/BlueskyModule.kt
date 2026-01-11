package com.zhangke.fread.bluesky

import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccountManager
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.adapter.BlueskyFeedsAdapter
import com.zhangke.fread.bluesky.internal.adapter.BlueskyNotificationAdapter
import com.zhangke.fread.bluesky.internal.adapter.BlueskyProfileAdapter
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.content.BlueskyContentManager
import com.zhangke.fread.bluesky.internal.migrate.BlueskyContentMigrator
import com.zhangke.fread.bluesky.internal.repo.BlueskyLoggedAccountRepo
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentViewModel
import com.zhangke.fread.bluesky.internal.screen.feeds.detail.FeedsDetailViewModel
import com.zhangke.fread.bluesky.internal.screen.feeds.explorer.ExplorerFeedsViewModel
import com.zhangke.fread.bluesky.internal.screen.feeds.following.BskyFollowingFeedsViewModel
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsContainerViewModel
import com.zhangke.fread.bluesky.internal.screen.home.BlueskyHomeContainerViewModel
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostViewModel
import com.zhangke.fread.bluesky.internal.screen.search.SearchStatusViewModel
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailViewModel
import com.zhangke.fread.bluesky.internal.screen.user.edit.EditProfileViewModel
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListViewModel
import com.zhangke.fread.bluesky.internal.uri.platform.PlatformUriTransformer
import com.zhangke.fread.bluesky.internal.uri.user.UserUriTransformer
import com.zhangke.fread.bluesky.internal.usecase.BskyStatusInteractiveUseCase
import com.zhangke.fread.bluesky.internal.usecase.CreateRecordUseCase
import com.zhangke.fread.bluesky.internal.usecase.DeleteRecordUseCase
import com.zhangke.fread.bluesky.internal.usecase.GetAllListsUseCase
import com.zhangke.fread.bluesky.internal.usecase.GetAtIdentifierUseCase
import com.zhangke.fread.bluesky.internal.usecase.GetCompletedNotificationUseCase
import com.zhangke.fread.bluesky.internal.usecase.GetFeedsStatusUseCase
import com.zhangke.fread.bluesky.internal.usecase.GetFollowingFeedsUseCase
import com.zhangke.fread.bluesky.internal.usecase.GetStatusContextUseCase
import com.zhangke.fread.bluesky.internal.usecase.LoginToBskyUseCase
import com.zhangke.fread.bluesky.internal.usecase.PinFeedsUseCase
import com.zhangke.fread.bluesky.internal.usecase.PublishingPostUseCase
import com.zhangke.fread.bluesky.internal.usecase.RefreshSessionUseCase
import com.zhangke.fread.bluesky.internal.usecase.UnblockUserWithoutUriUseCase
import com.zhangke.fread.bluesky.internal.usecase.UnpinFeedsUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdateBlockUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdateHomeTabUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdatePinnedFeedsOrderUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdatePreferencesUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdateProfileRecordUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdateRelationshipUseCase
import com.zhangke.fread.bluesky.internal.usecase.UploadBlobUseCase
import com.zhangke.fread.status.IStatusProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val blueskyModule = module {

    createPlatformModule()

    singleOf(::BlueskyContentManager)
    singleOf(::BlueskyScreenProvider)
    singleOf(::BlueskyLoggedAccountRepo)
    singleOf(::BlueskyClientManager)
    singleOf(::BlueskyPlatformRepo)

    factoryOf(::BlueskyAccountManager)
    factoryOf(::BlueskyPublishManager)
    factoryOf(::BlueskySearchEngine)
    factoryOf(::BlueskyNotificationResolver)
    factoryOf(::BlueskyStatusResolver)
    factoryOf(::BlueskyStatusSourceResolver)
    factoryOf(::BskyStartup)
    factoryOf(::BskyUrlInterceptor)
    factoryOf(::BlueskyContentMigrator)
    factoryOf(::BlueskyLoggedAccountManager)

    factoryOf(::BlueskyStatusAdapter)
    factoryOf(::BlueskyAccountAdapter)
    factoryOf(::BlueskyNotificationAdapter)
    factoryOf(::BlueskyFeedsAdapter)
    factoryOf(::BlueskyProfileAdapter)
    factoryOf(::UserUriTransformer)
    factoryOf(::PlatformUriTransformer)
    factoryOf(::LoginToBskyUseCase)
    factoryOf(::UpdateBlockUseCase)
    factoryOf(::UpdateHomeTabUseCase)
    factoryOf(::UpdatePinnedFeedsOrderUseCase)
    factoryOf(::UnpinFeedsUseCase)
    factoryOf(::UpdateRelationshipUseCase)
    factoryOf(::UpdatePreferencesUseCase)
    factoryOf(::GetAllListsUseCase)
    factoryOf(::GetFollowingFeedsUseCase)
    factoryOf(::GetFeedsStatusUseCase)
    factoryOf(::GetCompletedNotificationUseCase)
    factoryOf(::GetStatusContextUseCase)
    factoryOf(::GetAtIdentifierUseCase)
    factoryOf(::CreateRecordUseCase)
    factoryOf(::DeleteRecordUseCase)
    factoryOf(::PublishingPostUseCase)
    factoryOf(::UploadBlobUseCase)
    factoryOf(::UnblockUserWithoutUriUseCase)
    factoryOf(::BskyStatusInteractiveUseCase)
    factoryOf(::UpdateProfileRecordUseCase)
    factoryOf(::PinFeedsUseCase)
    factoryOf(::RefreshSessionUseCase)
    factoryOf(::AddBlueskyContentViewModel)
    factoryOf(::BlueskyHomeContainerViewModel)
    factoryOf(::HomeFeedsContainerViewModel)
    factoryOf(::BskyFollowingFeedsViewModel)
    factoryOf(::ExplorerFeedsViewModel)
    factoryOf(::FeedsDetailViewModel)
    factoryOf(::SearchStatusViewModel)
    factoryOf(::BskyUserDetailViewModel)
    factoryOf(::EditProfileViewModel)
    factoryOf(::UserListViewModel)
    factoryOf(::PublishPostViewModel)

    factoryOf(::BlueskyProvider) bind IStatusProvider::class
}

expect fun Module.createPlatformModule()
