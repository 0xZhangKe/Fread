package com.zhangke.fread.bluesky

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentScreen
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.feeds.explorer.ExplorerFeedsScreen
import com.zhangke.fread.bluesky.internal.screen.feeds.explorer.ExplorerFeedsScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.feeds.following.BskyFollowingFeedsPage
import com.zhangke.fread.bluesky.internal.screen.feeds.following.BskyFollowingFeedsPageNavKey
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsScreen
import com.zhangke.fread.bluesky.internal.screen.feeds.home.HomeFeedsScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostScreen
import com.zhangke.fread.bluesky.internal.screen.publish.PublishPostScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.search.SearchStatusScreen
import com.zhangke.fread.bluesky.internal.screen.search.SearchStatusScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailScreen
import com.zhangke.fread.bluesky.internal.screen.user.detail.BskyUserDetailScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.user.edit.EditProfileScreen
import com.zhangke.fread.bluesky.internal.screen.user.edit.EditProfileScreenNavKey
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListScreen
import com.zhangke.fread.bluesky.internal.screen.user.list.UserListScreenNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class BlueskyNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<BskyFollowingFeedsPageNavKey> { key ->
            BskyFollowingFeedsPage(
                koinViewModel { parametersOf(key.contentId, key.locator) }
            )
        }
        entry<HomeFeedsScreenNavKey> { key ->
            HomeFeedsScreen(
                feedsJson = key.feedsJson,
                locator = key.locator,
            )
        }
        entry<SearchStatusScreenNavKey> {
            SearchStatusScreen(koinViewModel())
        }
        entry<AddBlueskyContentScreenNavKey> { key ->
            AddBlueskyContentScreen(
                koinViewModel {
                    parametersOf(
                        key.baseUrl,
                        key.loginMode,
                        key.avatar,
                        key.displayName,
                        key.handle,
                    )
                }
            )
        }
        entry<PublishPostScreenNavKey> { key ->
            PublishPostScreen(
                koinViewModel {
                    parametersOf(
                        key.locator,
                        key.defaultText,
                        key.replyToJsonString,
                        key.quoteJsonString,
                    )
                }
            )
        }
        entry<UserListScreenNavKey> { key ->
            UserListScreen(
                locator = key.locator,
                type = key.type,
                viewModel = koinViewModel {
                    parametersOf(
                        key.locator,
                        key.type,
                        key.postUri,
                        key.did,
                    )
                },
            )
        }
        entry<BskyUserDetailScreenNavKey> { key ->
            BskyUserDetailScreen(
                locator = key.locator,
                did = key.did,
                viewModel = koinViewModel { parametersOf(key.locator, key.did) },
            )
        }
        entry<EditProfileScreenNavKey> { key ->
            EditProfileScreen(
                koinViewModel { parametersOf(key.locator) }
            )
        }
        entry<ExplorerFeedsScreenNavKey> { key ->
            ExplorerFeedsScreen(
                locator = key.locator,
                inlineMode = false,
            )
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(BskyFollowingFeedsPageNavKey::class)
        subclass(HomeFeedsScreenNavKey::class)
        subclass(SearchStatusScreenNavKey::class)
        subclass(AddBlueskyContentScreenNavKey::class)
        subclass(PublishPostScreenNavKey::class)
        subclass(UserListScreenNavKey::class)
        subclass(BskyUserDetailScreenNavKey::class)
        subclass(EditProfileScreenNavKey::class)
        subclass(ExplorerFeedsScreenNavKey::class)
    }
}
