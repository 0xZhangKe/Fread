package com.zhangke.fread.commonbiz.shared.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.framework.utils.getDefaultLocale
import com.zhangke.framework.utils.languageCode
import com.zhangke.fread.common.routeScreen
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.blog.detail.RssBlogDetailScreen
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.isRss
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.krouter.KRouter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer

class InteractiveHandler(
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : IInteractiveHandler {

    override val mutableErrorMessageFlow = MutableSharedFlow<TextString>()
    override val mutableOpenScreenFlow = MutableSharedFlow<Screen>()

    private lateinit var onInteractiveHandleResult: suspend (InteractiveHandleResult) -> Unit

    private lateinit var coroutineScope: CoroutineScope

    private val screenProvider = statusProvider.screenProvider

    override val composedStatusInteraction = object : ComposedStatusInteraction {

        override fun onStatusInteractive(status: StatusUiState, type: StatusActionType) {
            this@InteractiveHandler.onStatusInteractive(status, type)
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

        override fun onMentionClick(
            role: IdentityRole,
            did: String,
            protocol: StatusProviderProtocol
        ) {
            this@InteractiveHandler.onMentionClick(role, did, protocol)
        }

        override fun onStatusClick(status: StatusUiState) {
            this@InteractiveHandler.onStatusClick(status)
        }

        override fun onBlockClick(role: IdentityRole, blog: Blog) {
            this@InteractiveHandler.onBlogClick(role, blog)
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

        override fun onBoostedClick(role: IdentityRole, status: StatusUiState) {
            this@InteractiveHandler.onBoostedClick(role, status)
        }

        override fun onFavouritedClick(role: IdentityRole, status: StatusUiState) {
            this@InteractiveHandler.onFavouritedClick(role, status)
        }

        override fun onTranslateClick(role: IdentityRole, status: StatusUiState) {
            this@InteractiveHandler.onTranslateClick(role, status)
        }

        override fun onShowOriginalClick(status: StatusUiState) {
            this@InteractiveHandler.onShowOriginalClick(status)
        }
    }

    override fun initInteractiveHandler(
        coroutineScope: CoroutineScope,
        onInteractiveHandleResult: suspend (InteractiveHandleResult) -> Unit,
    ) {
        this.coroutineScope = coroutineScope
        this.onInteractiveHandleResult = onInteractiveHandleResult
        coroutineScope.launch {
            statusUpdater.statusUpdateFlow.collect {
                onInteractiveHandleResult(InteractiveHandleResult.UpdateStatus(it))
            }
        }
    }

    override fun onStatusInteractive(status: StatusUiState, type: StatusActionType) {
        if (type == StatusActionType.REPLY) {
            coroutineScope.launch {
                screenProvider.getReplyBlogScreen(status.role, status.status.intrinsicBlog)
                    ?.let(::tryOpenScreenByRoute)
            }
            return
        }
        if (type == StatusActionType.EDIT) {
            coroutineScope.launch {
                screenProvider.getEditBlogScreen(status.role, status.status.intrinsicBlog)
                    ?.let(::tryOpenScreenByRoute)
            }
            return
        }
        coroutineScope.launch {
            val result = statusProvider.statusResolver
                .interactive(status.role, status.status, type)
                .map { s -> s?.let { buildStatusUiState(status, it) } }
            if (result.isFailure) {
                mutableErrorMessageFlow.emitTextMessageFromThrowable(result.exceptionOrThrow())
                return@launch
            }
            val statusUiState = result.getOrNull()
            if (statusUiState == null) {
                onInteractiveHandleResult(InteractiveHandleResult.DeleteStatus(status.status.id))
            } else {
                val interactiveResult = InteractiveHandleResult.UpdateStatus(statusUiState)
                statusUpdater.update(statusUiState)
                onInteractiveHandleResult(interactiveResult)
            }
        }
    }

    override fun onUserInfoClick(role: IdentityRole, blogAuthor: BlogAuthor) {
        coroutineScope.launch {
            screenProvider.getUserDetailScreen(role, blogAuthor.uri)
                ?.let { mutableOpenScreenFlow.emit(it) }
        }
    }

    override fun onStatusClick(status: StatusUiState) {
        coroutineScope.launch {
            val screen = if (status.status.intrinsicBlog.platform.protocol.isRss) {
                RssBlogDetailScreen(status.status.intrinsicBlog)
            } else {
                StatusContextScreen(
                    role = status.role,
                    serializedStatus = refactorToNewBlog(status.status).let {
                        globalJson.encodeToString(Status.serializer(), it)
                    },
                    blogTranslationUiState = status.blogTranslationState,
                )
            }
            mutableOpenScreenFlow.emit(screen)
        }
    }

    override fun onBlogClick(role: IdentityRole, blog: Blog) {
        coroutineScope.launch {
            val screen = if (blog.platform.protocol.isRss) {
                RssBlogDetailScreen(blog)
            } else {
                StatusContextScreen(
                    role = role,
                    serializedBlog = globalJson.encodeToString(serializer(), blog),
                    blogTranslationUiState = null,
                )
            }
            mutableOpenScreenFlow.emit(screen)
        }
    }

    override fun onVoted(status: StatusUiState, votedOption: List<BlogPoll.Option>) {
        coroutineScope.launch {
            val result = statusProvider.statusResolver
                .votePoll(status.role, status.status, votedOption)
                .map { buildStatusUiState(status, it) }
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
            screenProvider.getUserDetailScreen(
                role = role,
                webFinger = mention.webFinger,
                protocol = mention.protocol,
            )?.let { mutableOpenScreenFlow.emit(it) }
        }
    }

    override fun onMentionClick(role: IdentityRole, did: String, protocol: StatusProviderProtocol) {
        coroutineScope.launch {
            screenProvider.getUserDetailRoute(
                role = role,
                did = did,
                protocol = protocol,
            )?.let { mutableOpenScreenFlow.emit(it) }
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

    private fun onBoostedClick(role: IdentityRole, status: StatusUiState) {
        screenProvider.getBlogBoostedScreen(
            role = role,
            blogId = status.status.intrinsicBlog.id,
            protocol = status.status.intrinsicBlog.platform.protocol,
        )?.let(::tryOpenScreenByRoute)
    }

    private fun onFavouritedClick(role: IdentityRole, status: StatusUiState) {
        screenProvider.getBlogFavouritedScreen(
            role = role,
            blogId = status.status.intrinsicBlog.id,
            protocol = status.status.intrinsicBlog.platform.protocol,
        )?.let(::tryOpenScreenByRoute)
    }

    private fun onTranslateClick(role: IdentityRole, status: StatusUiState) {
        coroutineScope.launch {
            onInteractiveHandleResult(InteractiveHandleResult.UpdateStatus(status.translating()))
            statusProvider.statusResolver
                .translate(role, status.status, getDefaultLocale().languageCode)
                .onFailure {
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(it)
                    onInteractiveHandleResult(InteractiveHandleResult.UpdateStatus(status.translateFinish()))
                }.onSuccess {
                    onInteractiveHandleResult(
                        InteractiveHandleResult.UpdateStatus(status.translated(it))
                    )
                }
        }
    }

    private fun onShowOriginalClick(status: StatusUiState) {
        coroutineScope.launch {
            val showOriginalBlog = status.copy(
                blogTranslationState = status.blogTranslationState.copy(
                    showingTranslation = false,
                )
            )
            onInteractiveHandleResult(InteractiveHandleResult.UpdateStatus(showOriginalBlog))
        }
    }

    private fun StatusUiState.translating(): StatusUiState {
        return this.copy(
            blogTranslationState = this.blogTranslationState.copy(
                translating = true,
            )
        )
    }

    private fun StatusUiState.translateFinish(): StatusUiState {
        return this.copy(
            blogTranslationState = this.blogTranslationState.copy(
                translating = false,
            )
        )
    }

    private fun StatusUiState.translated(translation: BlogTranslation): StatusUiState {
        return this.copy(
            blogTranslationState = this.blogTranslationState.copy(
                translating = false,
                blogTranslation = translation,
                showingTranslation = true,
            )
        )
    }

    private fun tryOpenScreenByRoute(route: String) = coroutineScope.launch {
        KRouter.routeScreen(route)
            ?.let { mutableOpenScreenFlow.emit(it) }
    }
}
