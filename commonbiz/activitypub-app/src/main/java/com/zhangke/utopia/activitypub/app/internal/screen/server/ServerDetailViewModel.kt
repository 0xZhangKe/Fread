package com.zhangke.utopia.activitypub.app.internal.screen.server

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.activitypub.app.internal.screen.server.adapter.ServiceDetailUiStateAdapter
import com.zhangke.utopia.activitypub.app.internal.usecase.GetInstanceUseCase
import com.zhangke.utopia.activitypub.app.internal.utils.toDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
internal class ServerDetailViewModel @Inject constructor(
    private val getInstance: GetInstanceUseCase,
    private val uiStateAdapter: ServiceDetailUiStateAdapter,
) : ViewModel() {

    private lateinit var baseUrl: String

    private val _uiState = MutableStateFlow(
        ServerDetailUiState(
            loading = true,
            domain = "",
            title = "",
            description = "",
            thumbnail = "",
            version = "",
            activeMonth = 0,
            languages = emptyList(),
            rules = emptyList(),
            tabs = listOf(
                ServerDetailTab.TRENDS,
                ServerDetailTab.TRENDS_TAG,
                ServerDetailTab.PLACEHOLDER,
                ServerDetailTab.ABOUT,
            )
        )
    )

    val uiState: StateFlow<ServerDetailUiState> = _uiState

    fun setupRoute(route: String) {
        baseUrl = route.split("=").last()
    }

    fun onPageResume() {
        launchInViewModel {
            getInstance(baseUrl.toDomain())
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
