package com.zhangke.fread.activitypub.app

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.activitypub.app.internal.screen.content.edit.EditContentConfigScreen
import com.zhangke.fread.activitypub.app.internal.screen.content.edit.EditContentConfigScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreen
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenKey
import com.zhangke.fread.activitypub.app.internal.screen.status.post.PostStatusScreenRoute
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class ActivityPubNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
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
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(PostStatusScreenKey::class)
        subclass(EditContentConfigScreenKey::class)
    }
}
