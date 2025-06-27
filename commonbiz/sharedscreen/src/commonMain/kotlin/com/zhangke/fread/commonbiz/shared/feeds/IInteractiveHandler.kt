package com.zhangke.fread.commonbiz.shared.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
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
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface IInteractiveHandler {

    val mutableErrorMessageFlow: MutableSharedFlow<TextString>
    val errorMessageFlow: SharedFlow<TextString> get() = mutableErrorMessageFlow

    val mutableOpenScreenFlow: MutableSharedFlow<Screen>
    val openScreenFlow: SharedFlow<Screen> get() = mutableOpenScreenFlow

    val composedStatusInteraction: ComposedStatusInteraction

    fun initInteractiveHandler(
        coroutineScope: CoroutineScope,
        onInteractiveHandleResult: suspend (InteractiveHandleResult) -> Unit,
    )

    fun onStatusInteractive(status: StatusUiState, type: StatusActionType)

    fun onUserInfoClick(locator: PlatformLocator, blogAuthor: BlogAuthor)

    fun onStatusClick(status: StatusUiState)

    fun onBlogClick(locator: PlatformLocator, blog: Blog)

    fun onVoted(status: StatusUiState, votedOption: List<BlogPoll.Option>)

    fun onFollowClick(locator: PlatformLocator, target: BlogAuthor)

    fun onUnfollowClick(locator: PlatformLocator, target: BlogAuthor)

    fun onMentionClick(locator: PlatformLocator, mention: Mention)

    fun onMentionClick(locator: PlatformLocator, did: String, protocol: StatusProviderProtocol)

    fun onHashtagClick(locator: PlatformLocator, tag: HashtagInStatus)

    fun onHashtagClick(locator: PlatformLocator, tag: Hashtag)

    fun onMaybeHashtagClick(locator: PlatformLocator, protocol: StatusProviderProtocol, tag: String)
}
