package com.zhangke.fread.activitypub.app.di

import com.zhangke.framework.module.ModuleStartup
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.ActivityPubProvider
import com.zhangke.fread.activitypub.app.ActivityPubStartup
import com.zhangke.fread.activitypub.app.ActivityPubUrlInterceptor
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.activitypub.app.internal.screen.account.EditAccountInfoViewModel
import com.zhangke.fread.activitypub.app.internal.screen.add.AddActivityPubContentViewModel
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
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenParams
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusViewModel
import com.zhangke.fread.activitypub.app.internal.screen.trending.TrendingStatusViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailContainerViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.about.UserAboutContainerViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListType
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.search.SearchUserViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListType
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.tags.TagListViewModel
import com.zhangke.fread.activitypub.app.internal.screen.user.timeline.UserTimelineContainerViewModel
import com.zhangke.fread.common.browser.BrowserInterceptor
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

expect interface ActivityPubPlatformComponent

interface ActivityPubComponent : ActivityPubPlatformComponent {

    val accountRepo: ActivityPubLoggedAccountRepo

    @IntoSet
    @Provides
    fun provideActivityPubProvider(activityPubProvider: ActivityPubProvider): IStatusProvider {
        return activityPubProvider
    }

    @IntoSet
    @Provides
    fun provideActivityPubUrlInterceptor(activityPubUrlInterceptor: ActivityPubUrlInterceptor): BrowserInterceptor {
        return activityPubUrlInterceptor
    }

    @IntoMap
    @Provides
    fun provideEditAccountInfoViewModel(creator: (FormalBaseUrl, FormalUri) -> EditAccountInfoViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return EditAccountInfoViewModel::class to EditAccountInfoViewModel.Factory { baseUrl, accountUri ->
            creator(baseUrl, accountUri)
        }
    }

    @IntoMap
    @Provides
    fun provideEditContentConfigViewModel(creator: (String) -> EditContentConfigViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return EditContentConfigViewModel::class to EditContentConfigViewModel.Factory { configId ->
            creator(configId)
        }
    }

    @IntoMap
    @Provides
    fun provideActivityPubTimelineContainerViewModel(creator: () -> ActivityPubTimelineContainerViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return ActivityPubTimelineContainerViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideActivityPubContentViewModel(creator: () -> ActivityPubContentViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return ActivityPubContentViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideEditFilterViewModel(creator: (PlatformLocator, String?) -> EditFilterViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return EditFilterViewModel::class to EditFilterViewModel.Factory { role, id ->
            creator(role, id)
        }
    }

    @IntoMap
    @Provides
    fun provideFiltersListViewModel(creator: (PlatformLocator) -> FiltersListViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return FiltersListViewModel::class to FiltersListViewModel.Factory { role ->
            creator(role)
        }
    }

    @IntoMap
    @Provides
    fun provideHashtagTimelineContainerViewModel(creator: () -> HashtagTimelineContainerViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return HashtagTimelineContainerViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideExplorerContainerViewModel(creator: () -> ExplorerContainerViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return ExplorerContainerViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideServerAboutViewModel(creator: () -> ServerAboutViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return ServerAboutViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideServerTrendsTagsViewModel(creator: () -> ServerTrendsTagsViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return ServerTrendsTagsViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideInstanceDetailViewModel(creator: (FormalBaseUrl) -> InstanceDetailViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return InstanceDetailViewModel::class to InstanceDetailViewModel.Factory { serverBaseUrl ->
            creator(serverBaseUrl)
        }
    }

    @IntoMap
    @Provides
    fun providePostStatusViewModel(creator: (PostStatusScreenParams) -> PostStatusViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return PostStatusViewModel::class to PostStatusViewModel.Factory { screenParams ->
            creator(screenParams)
        }
    }

    @IntoMap
    @Provides
    fun provideTrendingStatusViewModel(creator: () -> TrendingStatusViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return TrendingStatusViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideUserAboutContainerViewModel(creator: () -> UserAboutContainerViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return UserAboutContainerViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideUserListViewModel(creator: (PlatformLocator, UserListType, String?, FormalUri?, String?) -> UserListViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return UserListViewModel::class to UserListViewModel.Factory { locator, type, statusId, userUri, userId ->
            creator(locator, type, statusId, userUri, userId)
        }
    }

    @IntoMap
    @Provides
    fun provideStatusListViewModel(creator: (PlatformLocator, StatusListType) -> StatusListViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return StatusListViewModel::class to StatusListViewModel.Factory { locator, type ->
            creator(locator, type)
        }
    }

    @IntoMap
    @Provides
    fun provideTagListViewModel(creator: (PlatformLocator) -> TagListViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return TagListViewModel::class to TagListViewModel.Factory { role ->
            creator(role)
        }
    }

    @IntoMap
    @Provides
    fun provideUserTimelineContainerViewModel(creator: () -> UserTimelineContainerViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return UserTimelineContainerViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideUserDetailContainerViewModel(creator: () -> UserDetailContainerViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return UserDetailContainerViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideAddContentViewModel(creator: (BlogPlatform) -> AddActivityPubContentViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return AddActivityPubContentViewModel::class to AddActivityPubContentViewModel.Factory { role ->
            creator(role)
        }
    }

    @IntoMap
    @Provides
    fun provideCreatedListListViewModel(creator: (PlatformLocator) -> CreatedListsViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return CreatedListsViewModel::class to CreatedListsViewModel.Factory { role ->
            creator(role)
        }
    }

    @IntoMap
    @Provides
    fun provideSearchUserViewModel(creator: (PlatformLocator, Boolean) -> SearchUserViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return SearchUserViewModel::class to SearchUserViewModel.Factory { role, onlyFollowing ->
            creator(role, onlyFollowing)
        }
    }

    @IntoMap
    @Provides
    fun provideEditListViewModel(creator: (PlatformLocator, String) -> EditListViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return EditListViewModel::class to EditListViewModel.Factory { role, serializedList ->
            creator(role, serializedList)
        }
    }

    @IntoMap
    @Provides
    fun provideAddListViewModel(creator: (PlatformLocator) -> AddListViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return AddListViewModel::class to AddListViewModel.Factory { role ->
            creator(role)
        }
    }

    @IntoSet
    @Provides
    fun bindActivityPubStartup(module: ActivityPubStartup): ModuleStartup {
        return module
    }
}
