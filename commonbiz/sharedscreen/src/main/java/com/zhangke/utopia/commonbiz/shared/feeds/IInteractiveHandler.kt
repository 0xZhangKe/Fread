package com.zhangke.utopia.commonbiz.shared.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.ComposedStatusInteraction
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

    fun onVoted(status: StatusUiState, votedOption: List<BlogPoll.Option>)

    fun onFollowClick(role: IdentityRole, target: BlogAuthor)

    fun onUnfollowClick(role: IdentityRole, target: BlogAuthor)

    fun onMentionClick(role: IdentityRole, mention: Mention)

    fun onHashtagClick(role: IdentityRole, tag: HashtagInStatus)

    fun onHashtagClick(role: IdentityRole, tag: Hashtag)
}
