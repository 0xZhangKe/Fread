package com.zhangke.fread.commonbiz.shared

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.commonbiz.shared.blog.detail.RssBlogDetailScreen
import com.zhangke.fread.commonbiz.shared.blog.detail.RssBlogDetailScreenNavKey
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreen
import com.zhangke.fread.commonbiz.shared.screen.ImageViewerScreenNavKey
import com.zhangke.fread.commonbiz.shared.screen.SelectLanguageScreen
import com.zhangke.fread.commonbiz.shared.screen.SelectLanguageScreenNavKey
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishBlogScreen
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishBlogScreenNavKey
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.MultiAccountPublishingScreen
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.MultiAccountPublishingScreenKey
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreenNavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

class SharedScreenNavEntryProvider : NavEntryProvider {

    override fun EntryProviderScope<NavKey>.build() {
        entry<ImageViewerScreenNavKey> {
            ImageViewerScreen(
                selectedIndex = it.selectedIndex,
                sharedElementLabel = it.sharedElementLabel,
                imageList = it.imageList,
            )
        }
        entry<SelectLanguageScreenNavKey> {
            SelectLanguageScreen(
                selectedLanguages = it.selectedLanguages,
                maxSelectCount = it.maxSelectCount,
            )
        }
        entry<PublishBlogScreenNavKey> {
            PublishBlogScreen()
        }
        entry<MultiAccountPublishingScreenKey> {
            MultiAccountPublishingScreen(
                viewModel = koinViewModel { parametersOf(it.userUrisJson) }
            )
        }
        entry<StatusContextScreenNavKey> {
            StatusContextScreen(
                locator = it.locator,
                serializedStatus = it.serializedStatus,
                serializedBlog = it.serializedBlog,
                blogId = it.blogId,
                platform = it.platform,
                blogTranslationUiState = it.blogTranslationUiState,
                containerViewModel = koinViewModel(),
            )
        }
        entry<RssBlogDetailScreenNavKey> {
            RssBlogDetailScreen(
                serializedBlog = it.serializedBlog,
                viewModel = koinViewModel(),
            )
        }
    }

    override fun PolymorphicModuleBuilder<NavKey>.polymorph() {
        subclass(ImageViewerScreenNavKey::class)
        subclass(SelectLanguageScreenNavKey::class)
        subclass(PublishBlogScreenNavKey::class)
        subclass(MultiAccountPublishingScreenKey::class)
        subclass(StatusContextScreenNavKey::class)
        subclass(RssBlogDetailScreenNavKey::class)
    }
}
