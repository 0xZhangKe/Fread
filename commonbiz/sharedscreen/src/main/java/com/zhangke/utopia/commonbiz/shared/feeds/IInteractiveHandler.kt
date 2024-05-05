package com.zhangke.utopia.commonbiz.shared.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface IInteractiveHandler {

    val mutableErrorMessageFlow: MutableSharedFlow<TextString>
    val errorMessageFlow: SharedFlow<TextString> get() = mutableErrorMessageFlow

    val mutableOpenScreenFlow: MutableSharedFlow<Screen>
    val openScreenFlow: SharedFlow<Screen> get() = mutableOpenScreenFlow

    fun initInteractiveHandler(
        coroutineScope: CoroutineScope,
        roleResolver: IdentityRoleResolver,
        onInteractiveHandleResult: suspend (InteractiveHandleResult) -> Unit,
    )

    fun onStatusInteractive(status: Status, uiInteraction: StatusUiInteraction)

    fun onUserInfoClick(blogAuthor: BlogAuthor)

    fun onStatusClick(status: Status)

    fun onVoted(
        status: Status,
        votedOption: List<BlogPoll.Option>,
    )

    fun onFollowClick(target: BlogAuthor)

    fun onUnfollowClick(target: BlogAuthor)

    fun onMentionClick(author: BlogAuthor, mention: Mention)

    fun onHashtagClick(status: Status, tag: HashtagInStatus)

    fun onHashtagClick(author: BlogAuthor, tag: HashtagInStatus)

    fun onHashtagClick(tag: Hashtag)
}

interface IdentityRoleResolver {

    fun resolveRole(blogAuthor: BlogAuthor): IdentityRole

    fun resolveRole(tag: Hashtag): IdentityRole
}

abstract class DynamicAllInOneRoleResolver: IdentityRoleResolver {

    abstract fun getRole(): IdentityRole

    override fun resolveRole(blogAuthor: BlogAuthor) = getRole()

    override fun resolveRole(tag: Hashtag) = getRole()
}

class AllInOneRoleResolver(private val role: IdentityRole) : IdentityRoleResolver {

    override fun resolveRole(blogAuthor: BlogAuthor): IdentityRole {
        return role
    }

    override fun resolveRole(tag: Hashtag): IdentityRole {
        return role
    }
}
