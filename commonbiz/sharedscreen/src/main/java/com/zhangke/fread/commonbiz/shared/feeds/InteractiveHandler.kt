package com.zhangke.fread.commonbiz.shared.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.krouter.KRouter
import com.zhangke.fread.common.status.model.StatusUiInteraction
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.blog.detail.BlogDetailScreen
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.isRss
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class InteractiveHandler(
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : IInteractiveHandler {

    override val mutableErrorMessageFlow = MutableSharedFlow<TextString>()
    override val mutableOpenScreenFlow = MutableSharedFlow<Screen>()

    private lateinit var onInteractiveHandleResult: suspend (InteractiveHandleResult) -> Unit

    private lateinit var coroutineScope: CoroutineScope

    private val screenProvider = statusProvider.screenProvider

    override val composedStatusInteraction = object : ComposedStatusInteraction {

        override fun onStatusInteractive(status: StatusUiState, interaction: StatusUiInteraction) {
            this@InteractiveHandler.onStatusInteractive(status, interaction)
        }

        override fun onUserInfoClick(role: IdentityRole, blogAuthor: BlogAuthor) {
            this@InteractiveHandler.onUserInfoClick(role, blogAuthor)
        }

        override fun onVoted(status: StatusUiState, blogPollOptions: List<BlogPoll.Option>) {
            this@InteractiveHandler.onVoted(status, blogPollOptions)
        }

        override fun onHashtagInStatusClick(
            role: IdentityRole,
            hashtagInStatus: HashtagInStatus
        ) {
            this@InteractiveHandler.onHashtagClick(role, hashtagInStatus)
        }

        override fun onMentionClick(role: IdentityRole, mention: Mention) {
            this@InteractiveHandler.onMentionClick(role, mention)
        }

        override fun onStatusClick(status: StatusUiState) {
            this@InteractiveHandler.onStatusClick(status)
        }

        override fun onFollowClick(role: IdentityRole, target: BlogAuthor) {
            this@InteractiveHandler.onFollowClick(role, target)
        }

        override fun onUnfollowClick(role: IdentityRole, target: BlogAuthor) {
            this@InteractiveHandler.onUnfollowClick(role, target)
        }

        override fun onHashtagClick(role: IdentityRole, tag: Hashtag) {
            this@InteractiveHandler.onHashtagClick(role, tag)
        }
    }

    override fun initInteractiveHandler(
        coroutineScope: CoroutineScope,
        onInteractiveHandleResult: suspend (InteractiveHandleResult) -> Unit,
    ) {
        this.coroutineScope = coroutineScope
        this.onInteractiveHandleResult = onInteractiveHandleResult
    }

    override fun onStatusInteractive(status: StatusUiState, uiInteraction: StatusUiInteraction) {
        if (uiInteraction is StatusUiInteraction.Comment) {
            coroutineScope.launch {
                screenProvider.getReplyBlogScreen(status.role, status.status.intrinsicBlog)
                    ?.let(::tryOpenScreenByRoute)
            }
            return
        }
        coroutineScope.launch {
            val interaction = uiInteraction.statusInteraction ?: return@launch
            val result = statusProvider.statusResolver
                .interactive(status.role, status.status, interaction)
                .map { buildStatusUiState(status.role, it) }
            if (result.isFailure) {
                mutableErrorMessageFlow.emitTextMessageFromThrowable(result.exceptionOrThrow())
                return@launch
            }
            val interactiveResult = InteractiveHandleResult.UpdateStatus(result.getOrThrow())
            onInteractiveHandleResult(interactiveResult)
        }
    }

    override fun onUserInfoClick(role: IdentityRole, blogAuthor: BlogAuthor) {
        coroutineScope.launch {
            screenProvider.getUserDetailRoute(role, blogAuthor.uri)
                ?.let(::tryOpenScreenByRoute)
        }
    }

    override fun onStatusClick(status: StatusUiState) {
        coroutineScope.launch {
            val screen = if (status.status.intrinsicBlog.platform.protocol.isRss) {
                com.zhangke.fread.commonbiz.shared.blog.detail.BlogDetailScreen(status.status.intrinsicBlog)
            } else {
                StatusContextScreen(
                    role = status.role,
                    status = refactorToNewBlog(status.status),
                )
            }
            mutableOpenScreenFlow.emit(screen)
        }
    }

    override fun onVoted(status: StatusUiState, votedOption: List<BlogPoll.Option>) {
        coroutineScope.launch {
            val result = statusProvider.statusResolver
                .votePoll(status.role, status.status, votedOption)
                .map { buildStatusUiState(status.role, it) }
            if (result.isFailure) {
                mutableErrorMessageFlow.emitTextMessageFromThrowable(result.exceptionOrThrow())
                return@launch
            }
            val interactiveHandleResult = InteractiveHandleResult.UpdateStatus(result.getOrThrow())
            onInteractiveHandleResult(interactiveHandleResult)
        }
    }

    override fun onFollowClick(role: IdentityRole, target: BlogAuthor) {
        coroutineScope.launch {
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

    override fun onUnfollowClick(role: IdentityRole, target: BlogAuthor) {
        coroutineScope.launch {
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

    override fun onMentionClick(role: IdentityRole, mention: Mention) {
        coroutineScope.launch {
            screenProvider.getUserDetailRoute(
                role = role,
                webFinger = mention.webFinger,
                protocol = mention.protocol,
            )?.let(::tryOpenScreenByRoute)
        }
    }

    override fun onHashtagClick(role: IdentityRole, tag: HashtagInStatus) {
        openHashtagTimelineScreen(
            role = role,
            tag = tag.name,
            protocol = tag.protocol,
        )
    }

    override fun onHashtagClick(role: IdentityRole, tag: Hashtag) {
        openHashtagTimelineScreen(
            role = role,
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
