package com.zhangke.fread.activitypub.app

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.activitypub.app.internal.screen.account.EditAccountInfoScreen
import com.zhangke.fread.activitypub.app.internal.screen.account.EditAccountInfoScreenNavKey
import com.zhangke.fread.activitypub.app.internal.screen.add.AddActivityPubContentScreen
import com.zhangke.fread.activitypub.app.internal.screen.add.AddActivityPubContentScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.add.select.SelectPlatformScreen
import com.zhangke.fread.activitypub.app.internal.screen.add.select.SelectPlatformScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.content.edit.EditContentConfigScreen
import com.zhangke.fread.activitypub.app.internal.screen.content.edit.EditContentConfigScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.filters.edit.EditFilterScreen
import com.zhangke.fread.activitypub.app.internal.screen.filters.edit.EditFilterScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.filters.edit.HiddenKeywordScreen
import com.zhangke.fread.activitypub.app.internal.screen.filters.edit.HiddenKeywordScreenNavKey
import com.zhangke.fread.activitypub.app.internal.screen.filters.list.FiltersListScreen
import com.zhangke.fread.activitypub.app.internal.screen.filters.list.FiltersListScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineScreen
import com.zhangke.fread.activitypub.app.internal.screen.hashtag.HashtagTimelineScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.instance.InstanceDetailScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.list.CreatedListsScreen
import com.zhangke.fread.activitypub.app.internal.screen.list.CreatedListsScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.list.add.AddListScreen
import com.zhangke.fread.activitypub.app.internal.screen.list.add.AddListScreenNavKey
import com.zhangke.fread.activitypub.app.internal.screen.list.edit.EditListScreen
import com.zhangke.fread.activitypub.app.internal.screen.list.edit.EditListScreenNavKey
import com.zhangke.fread.activitypub.app.internal.screen.search.SearchStatusScreen
import com.zhangke.fread.activitypub.app.internal.screen.search.SearchStatusScreenNavKey
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.UserDetailScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.list.UserListScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.user.search.SearchUserScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.search.SearchUserScreenNavKey
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.status.StatusListScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.user.tags.TagListScreen
import com.zhangke.fread.activitypub.app.internal.screen.user.tags.TagListScreenKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class ActivityPubNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<SelectPlatformScreenKey> {
            SelectPlatformScreen(koinViewModel())
        }
        entry<AddActivityPubContentScreenKey> { key ->
            AddActivityPubContentScreen(
                platform = key.platform,
                viewModel = koinViewModel { parametersOf(key.platform) },
            )
        }
        entry<PostStatusScreenKey> {
            PostStatusScreen(
                viewModel = koinViewModel {
                    parametersOf(
                        PostStatusScreenRoute.buildParams(
                            accountUri = it.accountUri,
                            defaultContent = it.defaultContent,
                            editBlog = it.editBlogJsonString,
                            replyToBlogJsonString = it.replyingBlogJsonString,
                            quoteBlogJsonString = it.quoteBlogJsonString,
                        )
                    )
                }
            )
        }
        entry<EditContentConfigScreenKey> { key ->
            EditContentConfigScreen(
                contentId = key.contentId,
                viewModel = koinViewModel { parametersOf(key.contentId) },
            )
        }
        entry<InstanceDetailScreenKey> {
            InstanceDetailScreen(it.locator, koinViewModel { parametersOf(it.baseUrl) })
        }
        entry<UserDetailScreenKey> {
            UserDetailScreen(
                viewModel = koinViewModel(),
                locator = it.locator,
                userUri = it.userUri,
                webFinger = it.webFinger,
                userId = it.userId,
            )
        }
        entry<StatusListScreenKey> {
            StatusListScreen(it.locator, it.type)
        }
        entry<HashtagTimelineScreenKey> {
            HashtagTimelineScreen(
                viewModel = koinViewModel(),
                locator = it.locator,
                hashtag = it.hashtag,
            )
        }
        entry<UserListScreenKey> {
            UserListScreen(
                viewModel = koinViewModel {
                    parametersOf(
                        it.locator,
                        it.type,
                        it.statusId,
                        it.userUri,
                        it.userId,
                    )
                }
            )
        }
        entry<TagListScreenKey> {
            TagListScreen(
                viewModel = koinViewModel { parametersOf(it.locator) }
            )
        }
        entry<FiltersListScreenKey> {
            FiltersListScreen(
                viewModel = koinViewModel { parametersOf(it.locator) },
                locator = it.locator,
            )
        }
        entry<EditFilterScreenKey> {
            EditFilterScreen(
                viewModel = koinViewModel { parametersOf(it.locator, it.id) },
                id = it.id,
            )
        }
        entry<CreatedListsScreenKey> {
            CreatedListsScreen(
                viewModel = koinViewModel { parametersOf(it.locator) },
                locator = it.locator,
            )
        }
        entry<AddListScreenNavKey> {
            AddListScreen(
                viewModel = koinViewModel { parametersOf(it.locator) },
                locator = it.locator,
            )
        }
        entry<SearchUserScreenNavKey> {
            SearchUserScreen(
                viewModel = koinViewModel { parametersOf(it.locator, it.onlyFollowing) }
            )
        }
        entry<EditListScreenNavKey> {
            EditListScreen(
                locator = it.locator,
                viewModel = koinViewModel { parametersOf(it.locator, it.serializedList) },
            )
        }
        entry<EditAccountInfoScreenNavKey> {
            EditAccountInfoScreen(
                viewModel = koinViewModel { parametersOf(it.baseUrl, it.accountUri) },
            )
        }
        entry<SearchStatusScreenNavKey> {
            SearchStatusScreen(
                viewModel = koinViewModel { parametersOf(it.locator, it.userId) }
            )
        }
        entry<HiddenKeywordScreenNavKey> {
            HiddenKeywordScreen(it.addedKeywords)
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(SelectPlatformScreenKey::class)
        subclass(AddActivityPubContentScreenKey::class)
        subclass(PostStatusScreenKey::class)
        subclass(EditContentConfigScreenKey::class)
        subclass(InstanceDetailScreenKey::class)
        subclass(UserDetailScreenKey::class)
        subclass(StatusListScreenKey::class)
        subclass(HashtagTimelineScreenKey::class)
        subclass(UserListScreenKey::class)
        subclass(TagListScreenKey::class)
        subclass(FiltersListScreenKey::class)
        subclass(EditFilterScreenKey::class)
        subclass(CreatedListsScreenKey::class)
        subclass(AddListScreenNavKey::class)
        subclass(SearchUserScreenNavKey::class)
        subclass(EditListScreenNavKey::class)
        subclass(EditAccountInfoScreenNavKey::class)
        subclass(SearchStatusScreenNavKey::class)
        subclass(HiddenKeywordScreenNavKey::class)
    }
}
