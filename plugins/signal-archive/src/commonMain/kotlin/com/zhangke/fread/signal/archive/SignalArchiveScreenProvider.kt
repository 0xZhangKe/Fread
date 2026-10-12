package com.zhangke.fread.signal.archive

import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.nav.Tab
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.signal.archive.content.SignalArchiveContent
import com.zhangke.fread.signal.archive.screens.add.AddSignalArchiveContentNavKey
import com.zhangke.fread.signal.archive.screens.content.SignalArchiveContentTab
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.SIGNAL_ARCHIVE_ID
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.screen.IStatusScreenProvider
import com.zhangke.fread.status.uri.FormalUri

class SignalArchiveScreenProvider : IStatusScreenProvider {

    override fun getContentScreen(content: FreadContent, isLatestTab: Boolean): Tab? {
        return (content as? SignalArchiveContent)?.let { SignalArchiveContentTab(it, isLatestTab) }
    }

    override fun getAddContentScreen(protocol: StatusProviderProtocol): NavKey? {
        return if (protocol.id == SIGNAL_ARCHIVE_ID) AddSignalArchiveContentNavKey() else null
    }

    override fun getReplyBlogScreen(locator: PlatformLocator, blog: Blog): NavKey? {
        return null
    }

    override fun getEditBlogScreen(locator: PlatformLocator, blog: Blog): NavKey? {
        return null
    }

    override fun getQuoteBlogScreen(locator: PlatformLocator, blog: Blog): NavKey? {
        return null
    }

    override fun getEditContentConfigScreenScreen(content: FreadContent): NavKey? {
        return null
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        uri: FormalUri,
        userId: String?
    ): NavKey? {
        return null
    }

    override fun getUserDetailScreenWithoutAccount(uri: FormalUri): NavKey? {
        return null
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        webFinger: WebFinger,
        protocol: StatusProviderProtocol,
    ): NavKey? {
        return null
    }

    override fun getUserDetailScreen(
        locator: PlatformLocator,
        did: String,
        protocol: StatusProviderProtocol
    ): NavKey? {
        return null
    }

    override fun getTagTimelineScreen(
        locator: PlatformLocator,
        tag: String,
        protocol: StatusProviderProtocol,
    ): NavKey? {
        return null
    }

    override fun getBlogFavouritedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): NavKey? {
        return null
    }

    override fun getBlogBoostedScreen(
        locator: PlatformLocator,
        blog: Blog,
        protocol: StatusProviderProtocol
    ): NavKey? {
        return null
    }

    override fun getExplorerTab(locator: PlatformLocator, platform: BlogPlatform): Tab? {
        return null
    }
}
