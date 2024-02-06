package com.zhangke.utopia.activitypub.app.internal.screen.hashtag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubStatusLoadController
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.status.model.Status
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel(assistedFactory = HashtagTimelineViewModel.Factory::class)
class HashtagTimelineViewModel @AssistedInject constructor(
    private val baseUrlManager: BaseUrlManager,
    private val clientManager: ActivityPubClientManager,
    buildStatusUiState: BuildStatusUiStateUseCase,
    statusAdapter: ActivityPubStatusAdapter,
    platformRepo: ActivityPubPlatformRepo,
    interactiveHandler: ActivityPubInteractiveHandler,
    @Assisted private val hashtag: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(hashtag: String): HashtagTimelineViewModel
    }

    private val loadableController = ActivityPubStatusLoadController(
        coroutineScope = viewModelScope,
        statusAdapter = statusAdapter,
        platformRepo = platformRepo,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,

        )

    val errorMessageFlow = loadableController.errorMessageFlow

    val statusUiState = loadableController.uiState

    val hashtagTimelineUiState = MutableStateFlow(
        HashtagTimelineUiState(
            hashTag = hashtag,
            postsCount = 0,
        )
    )

    private var baseUrl: FormalBaseUrl? = null

    init {
        launchInViewModel {
            loadableController.initStatusData(
                baseUrl = getBaseUrl(),
                getStatusFromServer = {
                    loadHashtagTimeline(
                        baseUrl = it,
                        maxId = null,
                    )
                },
            )
        }
    }

    fun onRefresh() {
        launchInViewModel {
            loadableController.onRefresh(
                baseUrl = getBaseUrl(),
                getStatusFromServer = {
                    loadHashtagTimeline(
                        baseUrl = it,
                        maxId = null,
                    )
                },
            )
        }
    }

    fun onLoadMore() {
        launchInViewModel {
            loadableController.onLoadMore(
                baseUrl = getBaseUrl(),
                loadMoreFunction = { maxId, baseUrl ->
                    loadHashtagTimeline(
                        baseUrl = baseUrl,
                        maxId = maxId,
                    )
                },
            )
        }
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        loadableController.onInteractive(status, uiInteraction)
    }

    private suspend fun loadHashtagTimeline(
        baseUrl: FormalBaseUrl,
        maxId: String? = null,
    ): Result<List<ActivityPubStatusEntity>> {
        return clientManager.getClient(baseUrl)
            .timelinesRepo
            .getTagTimeline(
                hashtag = hashtag,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                maxId = maxId,
            )
    }

    private suspend fun getBaseUrl(): FormalBaseUrl {
        if (baseUrl != null) return baseUrl!!
        baseUrl = baseUrlManager.decideBaseUrl()
        return baseUrl!!
    }
}
