package com.zhangke.fread.activitypub.app.di

import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.ActivityPubContentManager
import com.zhangke.fread.activitypub.app.ActivityPubNotificationResolver
import com.zhangke.fread.activitypub.app.ActivityPubNavEntryProvider
import com.zhangke.fread.activitypub.app.ActivityPubProvider
import com.zhangke.fread.activitypub.app.ActivityPubPublishManager
import com.zhangke.fread.activitypub.app.ActivityPubScreenProvider
import com.zhangke.fread.activitypub.app.ActivityPubSearchEngine
import com.zhangke.fread.activitypub.app.ActivityPubSourceResolver
import com.zhangke.fread.activitypub.app.ActivityPubStartup
import com.zhangke.fread.activitypub.app.ActivityPubStatusResolver
import com.zhangke.fread.activitypub.app.ActivityPubUrlInterceptor
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubApplicationEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubBlogMetaAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubContentAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubCustomEmojiEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubPlatformEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubSearchAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubTranslationEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.PostStatusAttachmentAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.RegisterApplicationEntryAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubOAuthor
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.migrate.ActivityPubContentMigrator
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.activitypub.app.internal.repo.application.ActivityPubApplicationRepo
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.activitypub.app.internal.repo.platform.BlogPlatformResourceLoader
import com.zhangke.fread.activitypub.app.internal.repo.platform.MastodonInstanceRepo
import com.zhangke.fread.activitypub.app.internal.repo.status.ActivityPubStatusReadStateRepo
import com.zhangke.fread.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.fread.activitypub.app.internal.repo.user.UserRepo
import com.zhangke.fread.activitypub.app.internal.screen.account.EditAccountInfoViewModel
import com.zhangke.fread.activitypub.app.internal.screen.add.AddActivityPubContentViewModel
import com.zhangke.fread.activitypub.app.internal.screen.add.select.SelectPlatformViewModel
import com.zhangke.fread.activitypub.app.internal.screen.content.ActivityPubContentViewModel
import com.zhangke.fread.activitypub.app.internal.screen.content.edit.EditContentConfigViewModel
import com.zhangke.fread.activitypub.app.internal.screen.content.timeline.ActivityPubTimelineContainerViewModel
import com.zhangke.fread.activitypub.app.internal.screen.explorer.ExplorerContainerViewModel
import com.zhangke.fread.activitypub.app.internal.screen.filters.edit.EditFilterViewModel
import com.zhangke.fread.activitypub.app.internal.screen.filters.list.FiltersListViewModel
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineContainerViewModel
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailViewModel
import com.zhangke.fread.activitypub.app.internal.screen.instance.about.ServerAboutViewModel
import com.zhangke.fread.activitypub.app.internal.screen.instance.tags.ServerTrendsTagsViewModel
import com.zhangke.fread.activitypub.app.internal.screen.list.CreatedListsViewModel
import com.zhangke.fread.activitypub.app.internal.screen.list.add.AddListViewModel
import com.zhangke.fread.activitypub.app.internal.screen.list.edit.EditListViewModel
import com.zhangke.fread.activitypub.app.internal.screen.search.SearchStatusViewModel
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusViewModel
import com.zhangke.fread.activitypub.app.internal.screen.status.post.adapter.CustomEmojiAdapter
import com.zhangke.fread.activitypub.app.internal.screen.status.post.usecase.GenerateInitPostStatusUiStateUseCase
import com.zhangke.fread.activitypub.app.internal.screen.status.post.usecase.PublishPostUseCase
import com.zhangke.fread.activitypub.app.internal.screen.trending.TrendingStatusViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailContainerViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.search.SearchUserViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListContainerViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.tags.TagListViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.timeline.UserTimelineContainerViewModel
import com.zhangke.fread.activitypub.app.internal.source.UserSourceTransformer
import com.zhangke.fread.activitypub.app.internal.uri.PlatformUriTransformer
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.activitypub.app.internal.usecase.ActivityPubAccountLogoutUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.GetDefaultBaseUrlUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.GetInstanceAnnouncementUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.GetServerTrendTagsUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.UpdateActivityPubUserListUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.content.GetUserCreatedListUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.content.ReorderActivityPubTabUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.emoji.GetCustomEmojiUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.emoji.MapCustomEmojiUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.media.UploadMediaAttachmentUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.platform.GetInstancePostStatusRulesUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.source.user.SearchUserSourceNoTokenUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.GetStatusContextUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.GetTimelineStatusUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.GetUserStatusUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.VotePollUseCase
import com.zhangke.fread.activitypub.app.internal.utils.MastodonHelper
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.status.IStatusProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val activityPubModule = module {

    createPlatformModule()

    factoryOf(::ActivityPubNavEntryProvider) bind NavEntryProvider::class

    singleOf(::ActivityPubAccountManager)
    singleOf(::ActivityPubClientManager)
    singleOf(::ActivityPubOAuthor)
    singleOf(::LoggedAccountProvider)
    singleOf(::ActivityPubLoggedAccountRepo)
    singleOf(::MastodonHelper)

    factoryOf(::ActivityPubContentManager)
    factoryOf(::ActivityPubScreenProvider)
    factoryOf(::ActivityPubSearchEngine)
    factoryOf(::ActivityPubSourceResolver)
    factoryOf(::ActivityPubStatusResolver)
    factoryOf(::ActivityPubNotificationResolver)
    factoryOf(::ActivityPubPublishManager)
    factoryOf(::ActivityPubStartup)
    factoryOf(::ActivityPubUrlInterceptor) bind BrowserInterceptor::class
    factoryOf(::ActivityPubContentMigrator)

    factoryOf(::ActivityPubAccountEntityAdapter)
    factoryOf(::ActivityPubApplicationEntityAdapter)
    factoryOf(::ActivityPubBlogMetaAdapter)
    factoryOf(::ActivityPubContentAdapter)
    factoryOf(::ActivityPubCustomEmojiEntityAdapter)
    factoryOf(::ActivityPubInstanceAdapter)
    factoryOf(::ActivityPubLoggedAccountAdapter)
    factoryOf(::ActivityPubPlatformEntityAdapter)
    factoryOf(::ActivityPubPollAdapter)
    factoryOf(::ActivityPubSearchAdapter)
    factoryOf(::ActivityPubStatusAdapter)
    factoryOf(::ActivityPubTagAdapter)
    factoryOf(::ActivityPubTranslationEntityAdapter)
    factoryOf(::PostStatusAttachmentAdapter)
    factoryOf(::RegisterApplicationEntryAdapter)
    factoryOf(::CustomEmojiAdapter)

    factoryOf(::ActivityPubApplicationRepo)
    factoryOf(::ActivityPubPlatformRepo)
    factoryOf(::ActivityPubStatusReadStateRepo)
    factoryOf(::ActivityPubTimelineStatusRepo)
    factoryOf(::BlogPlatformResourceLoader)
    factoryOf(::MastodonInstanceRepo)
    factoryOf(::UserRepo)
    factoryOf(::WebFingerBaseUrlToUserIdRepo)

    factoryOf(::UserSourceTransformer)
    factoryOf(::PlatformUriTransformer)
    factoryOf(::UserUriTransformer)

    factoryOf(::ActivityPubAccountLogoutUseCase)
    factoryOf(::GetDefaultBaseUrlUseCase)
    factoryOf(::GetInstanceAnnouncementUseCase)
    factoryOf(::GetServerTrendTagsUseCase)
    factoryOf(::UpdateActivityPubUserListUseCase)
    factoryOf(::GetUserCreatedListUseCase)
    factoryOf(::ReorderActivityPubTabUseCase)
    factoryOf(::GetCustomEmojiUseCase)
    factoryOf(::MapCustomEmojiUseCase)
    factoryOf(::UploadMediaAttachmentUseCase)
    factoryOf(::GetInstancePostStatusRulesUseCase)
    factoryOf(::SearchUserSourceNoTokenUseCase)
    factoryOf(::GetStatusContextUseCase)
    factoryOf(::GetTimelineStatusUseCase)
    factoryOf(::GetUserStatusUseCase)
    factoryOf(::StatusInteractiveUseCase)
    factoryOf(::VotePollUseCase)
    factoryOf(::GenerateInitPostStatusUiStateUseCase)
    factoryOf(::PublishPostUseCase)

    viewModelOf(::EditAccountInfoViewModel)
    viewModelOf(::AddActivityPubContentViewModel)
    viewModelOf(::SelectPlatformViewModel)
    viewModelOf(::ActivityPubContentViewModel)
    viewModelOf(::EditContentConfigViewModel)
    viewModelOf(::ActivityPubTimelineContainerViewModel)
    viewModelOf(::ExplorerContainerViewModel)
    viewModelOf(::EditFilterViewModel)
    viewModelOf(::FiltersListViewModel)
    viewModelOf(::HashtagTimelineContainerViewModel)
    viewModelOf(::InstanceDetailViewModel)
    viewModelOf(::ServerAboutViewModel)
    viewModelOf(::ServerTrendsTagsViewModel)
    viewModelOf(::CreatedListsViewModel)
    viewModelOf(::AddListViewModel)
    viewModelOf(::EditListViewModel)
    viewModelOf(::SearchStatusViewModel)
    viewModelOf(::PostStatusViewModel)
    viewModelOf(::TrendingStatusViewModel)
    viewModelOf(::UserDetailContainerViewModel)
    viewModelOf(::UserListViewModel)
    viewModelOf(::SearchUserViewModel)
    viewModelOf(::StatusListContainerViewModel)
    viewModelOf(::TagListViewModel)
    viewModelOf(::UserTimelineContainerViewModel)

    factoryOf(::ActivityPubProvider) bind IStatusProvider::class
}

expect fun Module.createPlatformModule()
