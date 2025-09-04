package com.zhangke.fread.commonbiz.shared.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.framework.utils.getDefaultLocale
import com.zhangke.framework.utils.languageCode
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.routeScreen
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.blog.detail.RssBlogDetailScreen
import com.zhangke.fread.commonbiz.shared.screen.status.context.StatusContextScreen
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.isRss
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.krouter.KRouter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer

class InteractiveHandler(
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
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

        override fun onUserInfoClick(locator: PlatformLocator, blogAuthor: BlogAuthor) {
            this@InteractiveHandler.onUserInfoClick(locator, blogAuthor)
        }

        override fun onVoted(status: StatusUiState, blogPollOptions: List<BlogPoll.Option>) {
            this@InteractiveHandler.onVoted(status, blogPollOptions)
        }

        override fun onHashtagInStatusClick(
            locator: PlatformLocator,
            hashtagInStatus: HashtagInStatus
        ) {
            this@InteractiveHandler.onHashtagClick(locator, hashtagInStatus)
        }

        override fun onMaybeHashtagClick(
            locator: PlatformLocator,
            protocol: StatusProviderProtocol,
            hashtag: String,
        ) {
            this@InteractiveHandler.onMaybeHashtagClick(locator, protocol, hashtag)
        }

        override fun onMentionClick(locator: PlatformLocator, mention: Mention) {
            this@InteractiveHandler.onMentionClick(locator, mention)
        }

        override fun onMentionClick(
            locator: PlatformLocator,
            did: String,
            protocol: StatusProviderProtocol
        ) {
            this@InteractiveHandler.onMentionClick(locator, did, protocol)
        }

        override fun onStatusClick(status: StatusUiState) {
            this@InteractiveHandler.onStatusClick(status)
        }

        override fun onBlogClick(locator: PlatformLocator, blog: Blog) {
            this@InteractiveHandler.onBlogClick(locator, blog)
        }

        override fun onBlockClick(locator: PlatformLocator, blog: Blog) {
            this@InteractiveHandler.onBlogClick(locator, blog)
        }

        override fun onFollowClick(locator: PlatformLocator, target: BlogAuthor) {
            this@InteractiveHandler.onFollowClick(locator, target)
        }

        override fun onUnfollowClick(locator: PlatformLocator, target: BlogAuthor) {
            this@InteractiveHandler.onUnfollowClick(locator, target)
        }

        override fun onHashtagClick(locator: PlatformLocator, tag: Hashtag) {
            this@InteractiveHandler.onHashtagClick(locator, tag)
        }

        override fun onBoostedClick(locator: PlatformLocator, status: StatusUiState) {
            this@InteractiveHandler.onBoostedClick(locator, status)
        }

        override fun onFavouritedClick(locator: PlatformLocator, status: StatusUiState) {
            this@InteractiveHandler.onFavouritedClick(locator, status)
        }

        override fun onTranslateClick(locator: PlatformLocator, status: StatusUiState) {
            this@InteractiveHandler.onTranslateClick(locator, status)
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
            screenProvider.getReplyBlogScreen(status.locator, status.status.intrinsicBlog)
                ?.let(::openScreen)
            return
        }
        if (type == StatusActionType.EDIT) {
            screenProvider.getEditBlogScreen(status.locator, status.status.intrinsicBlog)
                ?.let(::openScreen)
            return
        }
        if (type == StatusActionType.QUOTE) {
            screenProvider.getQuoteBlogScreen(status.locator, status.status.intrinsicBlog)
                ?.let(::openScreen)
            return
        }
        coroutineScope.launch {
            val result = statusProvider.statusResolver
                .interactive(status.locator, status.status, type)
                .map { s -> s?.let { statusUiStateAdapter.toStatusUiState(status, it) } }
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

    override fun onUserInfoClick(locator: PlatformLocator, blogAuthor: BlogAuthor) {
        coroutineScope.launch {
            screenProvider.getUserDetailScreen(locator, blogAuthor.uri, blogAuthor.userId)
                ?.let { mutableOpenScreenFlow.emit(it) }
        }
    }

    override fun onStatusClick(status: StatusUiState) {
        coroutineScope.launch {
            val screen = if (status.status.intrinsicBlog.platform.protocol.isRss) {
                RssBlogDetailScreen(
                    serializedBlog = globalJson.encodeToString(status.status.intrinsicBlog),
                )
            } else {
                StatusContextScreen.create(refactorToNewStatus(status))
            }
            mutableOpenScreenFlow.emit(screen)
        }
    }

    override fun onBlogClick(locator: PlatformLocator, blog: Blog) {
        coroutineScope.launch {
            val screen = if (blog.platform.protocol.isRss) {
                RssBlogDetailScreen(
                    serializedBlog = globalJson.encodeToString(serializer(), blog),
                )
            } else {
                StatusContextScreen.create(
                    locator = locator,
                    blog = blog,
                )
            }
            mutableOpenScreenFlow.emit(screen)
        }
    }

    override fun onVoted(status: StatusUiState, votedOption: List<BlogPoll.Option>) {
        coroutineScope.launch {
            val result = statusProvider.statusResolver
                .votePoll(status.locator, status.status.intrinsicBlog, votedOption)
                .map { statusUiStateAdapter.toStatusUiState(status, it) }
            if (result.isFailure) {
                mutableErrorMessageFlow.emitTextMessageFromThrowable(result.exceptionOrThrow())
                return@launch
            }
            val interactiveHandleResult = InteractiveHandleResult.UpdateStatus(result.getOrThrow())
            onInteractiveHandleResult(interactiveHandleResult)
        }
    }

    override fun onFollowClick(locator: PlatformLocator, target: BlogAuthor) {
        coroutineScope.launch {
            statusProvider.statusResolver
                .follow(locator, target)
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

    override fun onUnfollowClick(locator: PlatformLocator, target: BlogAuthor) {
        coroutineScope.launch {
            statusProvider.statusResolver
                .unfollow(locator, target)
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

    override fun onMentionClick(locator: PlatformLocator, mention: Mention) {
        coroutineScope.launch {
            screenProvider.getUserDetailScreen(
                locator = locator,
                webFinger = mention.webFinger,
                protocol = mention.protocol,
            )?.let { mutableOpenScreenFlow.emit(it) }
        }
    }

    override fun onMentionClick(
        locator: PlatformLocator,
        did: String,
        protocol: StatusProviderProtocol
    ) {
        coroutineScope.launch {
            screenProvider.getUserDetailScreen(
                locator = locator,
                did = did,
                protocol = protocol,
            )?.let { mutableOpenScreenFlow.emit(it) }
        }
    }

    override fun onHashtagClick(locator: PlatformLocator, tag: HashtagInStatus) {
        openHashtagTimelineScreen(
            locator = locator,
            tag = tag.name,
            protocol = tag.protocol,
        )
    }

    override fun onHashtagClick(locator: PlatformLocator, tag: Hashtag) {
        openHashtagTimelineScreen(
            locator = locator,
            tag = tag.name,
            protocol = tag.protocol,
        )
    }

    override fun onMaybeHashtagClick(
        locator: PlatformLocator,
        protocol: StatusProviderProtocol,
        tag: String,
    ) {
        openHashtagTimelineScreen(locator = locator, tag = tag, protocol = protocol)
    }

    private fun openHashtagTimelineScreen(
        locator: PlatformLocator,
        tag: String,
        protocol: StatusProviderProtocol,
    ) {
        screenProvider.getTagTimelineScreen(
            locator = locator,
            tag = tag,
            protocol = protocol
        )?.let(::openScreen)
    }

    private fun onBoostedClick(locator: PlatformLocator, status: StatusUiState) {
        screenProvider.getBlogBoostedScreen(
            locator = locator,
            blog = status.status.intrinsicBlog,
            protocol = status.status.intrinsicBlog.platform.protocol,
        )?.let(::openScreen)
    }

    private fun onFavouritedClick(locator: PlatformLocator, status: StatusUiState) {
        screenProvider.getBlogFavouritedScreen(
            locator = locator,
            blog = status.status.intrinsicBlog,
            protocol = status.status.intrinsicBlog.platform.protocol,
        )?.let(::openScreen)
    }

    private fun onTranslateClick(locator: PlatformLocator, status: StatusUiState) {
        coroutineScope.launch {
            onInteractiveHandleResult(InteractiveHandleResult.UpdateStatus(status.translating()))
            statusProvider.statusResolver
                .translate(locator, status.status, getDefaultLocale().languageCode)
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

    private fun openScreen(screen: Screen) {
        coroutineScope.launch {
            mutableOpenScreenFlow.emit(screen)
        }
    }

    private fun tryOpenScreenByRoute(route: String) = coroutineScope.launch {
        KRouter.routeScreen(route)
            ?.let { mutableOpenScreenFlow.emit(it) }
    }
}
