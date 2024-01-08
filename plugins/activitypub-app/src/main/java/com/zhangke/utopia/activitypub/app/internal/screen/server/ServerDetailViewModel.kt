package com.zhangke.utopia.activitypub.app.internal.screen.server

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstance
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.screen.server.adapter.ServiceDetailUiStateAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
internal class ServerDetailViewModel @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
    private val uiStateAdapter: ServiceDetailUiStateAdapter,
) : ViewModel() {

    lateinit var serverBaseUrl: FormalBaseUrl

    private val _uiState = MutableStateFlow(
        ServerDetailUiState(
            loading = true,
            baseUrl = null,
            instance = ActivityPubInstance(
                baseUrl = FormalBaseUrl.build("", ""),
                title = "",
                description = "",
                thumbnail = "",
                version = "",
                activeMonth = 0,
                languages = emptyList(),
                rules = emptyList(),
            ),
            tabs = listOf(
                ServerDetailTab.TRENDS,
                ServerDetailTab.TRENDS_TAG,
                ServerDetailTab.PLACEHOLDER,
                ServerDetailTab.ABOUT,
            )
        )
    )

    val uiState: StateFlow<ServerDetailUiState> = _uiState

    fun onPageResume() {
        launchInViewModel {
            platformRepo.getInstanceEntity(serverBaseUrl)
                .onSuccess {
                    _uiState.value = uiStateAdapter.createUiState(
                        entity = it,
                        loading = false,
                        _uiState.value.tabs,
                    )
                }
        }
    }
}
