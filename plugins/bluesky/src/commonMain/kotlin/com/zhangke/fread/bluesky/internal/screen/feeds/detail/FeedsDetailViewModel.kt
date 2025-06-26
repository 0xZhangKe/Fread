package com.zhangke.fread.bluesky.internal.screen.feeds.detail

import androidx.lifecycle.ViewModel
import app.bsky.feed.GetFeedGeneratorsQueryParams
import app.bsky.feed.Like
import com.atproto.repo.StrongRef
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.bluesky.internal.adapter.BlueskyFeedsAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.client.BskyCollections
import com.zhangke.fread.bluesky.internal.client.adjustToRkey
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.usecase.CreateRecordUseCase
import com.zhangke.fread.bluesky.internal.usecase.DeleteRecordUseCase
import com.zhangke.fread.bluesky.internal.usecase.PinFeedsUseCase
import com.zhangke.fread.bluesky.internal.usecase.UnpinFeedsUseCase
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Cid

class FeedsDetailViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val feedsAdapter: BlueskyFeedsAdapter,
    private val createRecord: CreateRecordUseCase,
    private val deleteRecord: DeleteRecordUseCase,
    private val followFeeds: PinFeedsUseCase,
    private val unfollowFeeds: UnpinFeedsUseCase,
    @Assisted private val locator: PlatformLocator,
    @Assisted feeds: BlueskyFeeds,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            locator: PlatformLocator,
            feeds: BlueskyFeeds,
        ): FeedsDetailViewModel
    }

    private val _uiState = MutableStateFlow(FeedsDetailUiState.default(feeds))
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    private val _feedsUpdateFlow = MutableSharedFlow<BlueskyFeeds.Feeds>()
    val feedsUpdateFlow = _feedsUpdateFlow.asSharedFlow()

    private var likeJob: Job? = null
    private var pinJob: Job? = null

    init {
        getFeedsDetail()
    }

    fun onLikeClick() {
        if (likeJob?.isActive == true) return
        val feeds = _uiState.value.feeds
        if (feeds !is BlueskyFeeds.Feeds) return
        likeJob?.cancel()
        likeJob = launchInViewModel {
            if (feeds.liked) {
                deleteRecord(
                    locator = locator,
                    collection = BskyCollections.feedLike,
                    rkey = feeds.likedRecord!!.adjustToRkey(),
                )
            } else {
                createRecord(
                    locator = locator,
                    collection = BskyCollections.feedLike,
                    record = Like(
                        subject = StrongRef(
                            uri = AtUri(feeds.uri),
                            cid = Cid(feeds.cid),
                        ),
                        createdAt = Clock.System.now(),
                    ).bskyJson()
                )
            }.onSuccess {
                getFeedsDetail()
            }.onFailure {
                _snackBarMessageFlow.emitTextMessageFromThrowable(it)
            }
        }
    }

    fun onPinClick() {
        val feeds = _uiState.value.feeds
        if (feeds !is BlueskyFeeds.Feeds) return
        if (pinJob?.isActive == true) return
        pinJob?.cancel()
        pinJob = launchInViewModel {
            if (feeds.pinned) {
                unfollowFeeds(locator = locator, feeds = feeds)
            } else {
                followFeeds(locator = locator, feeds = feeds)
            }.onSuccess {
                val newFeeds = feeds.copy(pinned = !feeds.pinned)
                _uiState.update { state -> state.copy(feeds = newFeeds) }
                _feedsUpdateFlow.emit(newFeeds)
            }.onFailure {
                _snackBarMessageFlow.emitTextMessageFromThrowable(it)
            }
        }
    }

    private fun getFeedsDetail() {
        launchInViewModel {
            val feeds = _uiState.value.feeds
            if (feeds !is BlueskyFeeds.Feeds) return@launchInViewModel
            clientManager.getClient(locator)
                .getFeedGeneratorsCatching(GetFeedGeneratorsQueryParams(listOf(AtUri(feeds.uri))))
                .onSuccess { response ->
                    response.feeds.firstOrNull { it.uri.atUri == feeds.uri }
                        ?.let { generatorView ->
                            feedsAdapter.convertToFeeds(
                                generator = generatorView,
                                pinned = feeds.pinned,
                            )
                        }?.let { feed ->
                            _uiState.update { it.copy(feeds = feed) }
                            _feedsUpdateFlow.emit(feed)
                        }
                }
        }
    }
}
