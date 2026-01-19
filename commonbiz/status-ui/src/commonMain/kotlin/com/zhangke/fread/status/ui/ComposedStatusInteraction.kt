package com.zhangke.fread.status.ui

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform

interface ComposedStatusInteraction {

    fun onStatusInteractive(status: StatusUiState, type: StatusActionType)
    fun onUserInfoClick(locator: PlatformLocator, blogAuthor: BlogAuthor)
    fun onVoted(status: StatusUiState, blogPollOptions: List<BlogPoll.Option>)
    fun onHashtagInStatusClick(locator: PlatformLocator, hashtagInStatus: HashtagInStatus)
    fun onHashtagClick(locator: PlatformLocator, tag: Hashtag)
    fun onMaybeHashtagClick(
        locator: PlatformLocator,
        protocol: StatusProviderProtocol,
        hashtag: String
    )

    fun onMentionClick(locator: PlatformLocator, mention: Mention)
    fun onMentionClick(locator: PlatformLocator, did: String, protocol: StatusProviderProtocol)
    fun onStatusClick(status: StatusUiState)
    fun onBlogClick(locator: PlatformLocator, blog: Blog)
    fun onBlogIdClick(locator: PlatformLocator, platform: BlogPlatform, blogId: String)
    fun onBlockClick(locator: PlatformLocator, blog: Blog)
    fun onFollowClick(locator: PlatformLocator, target: BlogAuthor)
    fun onUnfollowClick(locator: PlatformLocator, target: BlogAuthor)
    fun onBoostedClick(locator: PlatformLocator, status: StatusUiState)
    fun onFavouritedClick(locator: PlatformLocator, status: StatusUiState)
    fun onTranslateClick(locator: PlatformLocator, status: StatusUiState)
    fun onShowOriginalClick(status: StatusUiState)
}
