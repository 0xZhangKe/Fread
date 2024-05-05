package com.zhangke.utopia.commonbiz.shared.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.krouter.KRouter
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.model.StatusProviderProtocol
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class InteractiveHandler(
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : IInteractiveHandler {

    override val mutableErrorMessageFlow = MutableSharedFlow<TextString>()
    override val mutableOpenScreenFlow = MutableSharedFlow<Screen>()

    private lateinit var roleResolver: IdentityRoleResolver
    private lateinit var onInteractiveHandleResult: suspend (InteractiveHandleResult) -> Unit

    private lateinit var coroutineScope: CoroutineScope

    private val screenProvider = statusProvider.screenProvider

    override fun initInteractiveHandler(
        coroutineScope: CoroutineScope,
        roleResolver: IdentityRoleResolver,
        onInteractiveHandleResult: suspend (InteractiveHandleResult) -> Unit,
    ) {
        this.coroutineScope = coroutineScope
        this.roleResolver = roleResolver
        this.onInteractiveHandleResult = onInteractiveHandleResult
    }

    override fun onStatusInteractive(
        status: Status,
        uiInteraction: StatusUiInteraction,
    ) {
        val role = roleResolver.resolveRole(status.intrinsicBlog.author)
        if (uiInteraction is StatusUiInteraction.Comment) {
            screenProvider.getReplyBlogScreen(role, status.intrinsicBlog)
                ?.let(::tryOpenScreenByRoute)
            return
        }
        coroutineScope.launch {
            val interaction = uiInteraction.statusInteraction ?: return@launch
            val result = statusProvider.statusResolver
                .interactive(role, status, interaction)
                .map { buildStatusUiState(it) }
            if (result.isFailure) {
                mutableErrorMessageFlow.emitTextMessageFromThrowable(result.exceptionOrThrow())
                return@launch
            }
            val interactiveResult = InteractiveHandleResult.UpdateStatus(result.getOrThrow())
            onInteractiveHandleResult(interactiveResult)
        }
    }

    override fun onUserInfoClick(blogAuthor: BlogAuthor) {
        coroutineScope.launch {
            screenProvider.getUserDetailRoute(roleResolver.resolveRole(blogAuthor), blogAuthor.uri)
                ?.let(::tryOpenScreenByRoute)
        }
    }

    override fun onStatusClick(status: Status) {
        coroutineScope.launch {
            mutableOpenScreenFlow.emit(
                StatusContextScreen(
                    role = roleResolver.resolveRole(status.intrinsicBlog.author),
                    status = status,
                )
            )
        }
    }

    override fun onVoted(
        status: Status,
        votedOption: List<BlogPoll.Option>,
    ) {
        coroutineScope.launch {
            val role = roleResolver.resolveRole(status.intrinsicBlog.author)
            val result = statusProvider.statusResolver.votePoll(role, status, votedOption)
                .map { buildStatusUiState(it) }
            if (result.isFailure) {
                mutableErrorMessageFlow.emitTextMessageFromThrowable(result.exceptionOrThrow())
                return@launch
            }
            val interactiveHandleResult = InteractiveHandleResult.UpdateStatus(result.getOrThrow())
            onInteractiveHandleResult(interactiveHandleResult)
        }
    }

    override fun onFollowClick(target: BlogAuthor) {
        coroutineScope.launch {
            val role = roleResolver.resolveRole(target)
            statusProvider.statusResolver
                .follow(role, target)
                .onSuccess {
                    onInteractiveHandleResult(
                        InteractiveHandleResult.UpdateFollowState(
                            target.uri,
                            true
                        )
                    )
                }.onFailure {
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(it)
                }
        }
    }

    override fun onUnfollowClick(target: BlogAuthor) {
        coroutineScope.launch {
            val role = roleResolver.resolveRole(target)
            statusProvider.statusResolver
                .unfollow(role, target)
                .onSuccess {
                    onInteractiveHandleResult(
                        InteractiveHandleResult.UpdateFollowState(
                            target.uri,
                            false
                        )
                    )
                }.onFailure {
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(it)
                }
        }
    }

    override fun onMentionClick(author: BlogAuthor, mention: Mention) {
        coroutineScope.launch {
            val role = roleResolver.resolveRole(author)
            screenProvider.getUserDetailRoute(
                role = role,
                webFinger = mention.webFinger,
                protocol = mention.protocol,
            )?.let(::tryOpenScreenByRoute)
        }
    }

    override fun onHashtagClick(status: Status, tag: HashtagInStatus) {
        openHashtagTimelineScreen(
            role = roleResolver.resolveRole(status.intrinsicBlog.author),
            tag = tag.name,
            protocol = tag.protocol,
        )
    }

    override fun onHashtagClick(author: BlogAuthor, tag: HashtagInStatus) {
        openHashtagTimelineScreen(
            role = roleResolver.resolveRole(author),
            tag = tag.name,
            protocol = tag.protocol,
        )
    }

    override fun onHashtagClick(tag: Hashtag) {
        openHashtagTimelineScreen(
            role = roleResolver.resolveRole(tag),
            tag = tag.name,
            protocol = tag.protocol,
        )
    }

    private fun openHashtagTimelineScreen(
        role: IdentityRole,
        tag: String,
        protocol: StatusProviderProtocol,
    ) {
        screenProvider.getTagTimelineScreenRoute(
            role = role,
            tag = tag,
            protocol = protocol
        )?.let(::tryOpenScreenByRoute)
    }

    private fun tryOpenScreenByRoute(route: String) = coroutineScope.launch {
        KRouter.route<Screen>(route)
            ?.let { mutableOpenScreenFlow.emit(it) }
    }
}
