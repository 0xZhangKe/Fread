package com.zhangke.fread.bluesky

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentViewModel
import com.zhangke.fread.bluesky.internal.screen.feeds.detail.FeedsDetailViewModel
import com.zhangke.fread.bluesky.internal.screen.feeds.explorer.ExplorerFeedsViewModel
import com.zhangke.fread.bluesky.internal.screen.feeds.following.BskyFollowingFeedsViewModel
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsContainerViewModel
import com.zhangke.fread.bluesky.internal.screen.home.BlueskyHomeContainerViewModel
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostViewModel
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailViewModel
import com.zhangke.fread.bluesky.internal.screen.user.edit.EditProfileViewModel
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListType
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListViewModel
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.IntoMap
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

expect interface BlueskyPlatformComponent

interface BlueskyComponent : BlueskyPlatformComponent {

    @IntoSet
    @Provides
    fun provideActivityPubProvider(blueskyProvider: BlueskyProvider): IStatusProvider {
        return blueskyProvider
    }

    @IntoMap
    @Provides
    fun provideAddBlueskyContentViewModel(creator: (FormalBaseUrl, Boolean) -> AddBlueskyContentViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return AddBlueskyContentViewModel::class to AddBlueskyContentViewModel.Factory { baseUrl, loginMode ->
            creator(baseUrl, loginMode)
        }
    }

    @IntoMap
    @Provides
    fun provideBlueskyHomeContainerViewModel(creator: () -> BlueskyHomeContainerViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return BlueskyHomeContainerViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideBskyFeedsExplorerViewModel(creator: (String) -> BskyFollowingFeedsViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return BskyFollowingFeedsViewModel::class to BskyFollowingFeedsViewModel.Factory { contentId ->
            creator(contentId)
        }
    }

    @IntoMap
    @Provides
    fun provideUserDetailViewModel(creator: (IdentityRole, String) -> BskyUserDetailViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return BskyUserDetailViewModel::class to BskyUserDetailViewModel.Factory { role, did ->
            creator(role, did)
        }
    }

    @IntoMap
    @Provides
    fun provideEditProfileViewModel(creator: (IdentityRole) -> EditProfileViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return EditProfileViewModel::class to EditProfileViewModel.Factory { role ->
            creator(role)
        }
    }

    @IntoMap
    @Provides
    fun provideUserListViewModel(creator: (IdentityRole, UserListType, String?) -> UserListViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return UserListViewModel::class to UserListViewModel.Factory { role, type, postUri ->
            creator(role, type, postUri)
        }
    }

    @IntoMap
    @Provides
    fun providePublishPostViewModel(creator: (IdentityRole, String?, String?) -> PublishPostViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return PublishPostViewModel::class to PublishPostViewModel.Factory { role, reply, quote ->
            creator(role, reply, quote)
        }
    }

    @IntoMap
    @Provides
    fun provideHomeFeedsContainerViewModel(creator: () -> HomeFeedsContainerViewModel): Pair<ViewModelKey, ViewModelCreator> {
        return HomeFeedsContainerViewModel::class to creator
    }

    @IntoMap
    @Provides
    fun provideExplorerFeedsViewModel(creator: (IdentityRole) -> ExplorerFeedsViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return ExplorerFeedsViewModel::class to ExplorerFeedsViewModel.Factory { role ->
            creator(role)
        }
    }

    @IntoMap
    @Provides
    fun provideFeedsDetailViewModel(creator: (IdentityRole, BlueskyFeeds) -> FeedsDetailViewModel): Pair<ViewModelKey, ViewModelFactory> {
        return FeedsDetailViewModel::class to FeedsDetailViewModel.Factory { role, feeds ->
            creator(role, feeds)
        }
    }
}
