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
import com.zhangke.fread.bluesky.internal.usecase.FollowFeedsUseCase
import com.zhangke.fread.bluesky.internal.usecase.UnfollowFeedsUseCase
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.IdentityRole
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
    private val followFeeds: FollowFeedsUseCase,
    private val unfollowFeeds: UnfollowFeedsUseCase,
    @Assisted private val role: IdentityRole,
    @Assisted private val feeds: BlueskyFeeds.Feeds,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
            feeds: BlueskyFeeds.Feeds,
        ): FeedsDetailViewModel
    }

    private val _uiState = MutableStateFlow(FeedsDetailUiState.default(feeds))
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    private var likeJob: Job? = null
    private var pinJob: Job? = null

    init {
        getFeedsDetail()
    }

    fun onLikeClick() {
        if (likeJob?.isActive == true) return
        likeJob?.cancel()
        likeJob = launchInViewModel {
            if (feeds.liked) {
                deleteRecord(
                    role = role,
                    collection = BskyCollections.feedLike,
                    rkey = feeds.likedRecord!!.adjustToRkey(),
                )
            } else {
                createRecord(
                    role = role,
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
        if (pinJob?.isActive == true) return
        pinJob?.cancel()
        pinJob = launchInViewModel {
            if (feeds.pinned) {
                unfollowFeeds(role = role, feeds = feeds)
            } else {
                followFeeds(role = role, feeds = feeds)
            }.onSuccess {
                getFeedsDetail()
            }.onFailure {
                _snackBarMessageFlow.emitTextMessageFromThrowable(it)
            }
        }
    }

    private fun getFeedsDetail() {
        launchInViewModel {
            clientManager.getClient(role)
                .getFeedGeneratorsCatching(GetFeedGeneratorsQueryParams(listOf(AtUri(feeds.uri))))
                .onSuccess { response ->
                    response.feeds.firstOrNull { it.uri.atUri == feeds.uri }
                        ?.let { generatorView ->
                            feedsAdapter.convertToFeeds(
                                generator = generatorView,
                                following = false,
                                pinned = false,
                            )
                        }?.let { feed ->
                            _uiState.update { it.copy(feeds = feed) }
                        }
                }
        }
    }
}
