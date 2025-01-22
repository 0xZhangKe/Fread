package com.zhangke.fread.commonbiz.shared.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusProviderProtocol
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

    fun onStatusInteractive(status: StatusUiState, uiInteraction: StatusUiInteraction)

    fun onUserInfoClick(role: IdentityRole, blogAuthor: BlogAuthor)

    fun onStatusClick(status: StatusUiState)

    fun onBlogClick(role: IdentityRole, blog: Blog)

    fun onVoted(status: StatusUiState, votedOption: List<BlogPoll.Option>)

    fun onFollowClick(role: IdentityRole, target: BlogAuthor)

    fun onUnfollowClick(role: IdentityRole, target: BlogAuthor)

    fun onMentionClick(role: IdentityRole, mention: Mention)

    fun onMentionClick(role: IdentityRole, did: String, protocol: StatusProviderProtocol)

    fun onHashtagClick(role: IdentityRole, tag: HashtagInStatus)

    fun onHashtagClick(role: IdentityRole, tag: Hashtag)
}
