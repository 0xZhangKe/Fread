package com.zhangke.utopia.activitypubapp.screen.server

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.activitypubapp.screen.server.adapter.ServiceDetailUiStateAdapter
import com.zhangke.utopia.activitypubapp.usecase.GetInstanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
internal class ServerDetailViewModel @Inject constructor(
    private val getInstance: GetInstanceUseCase,
    private val uiStateAdapter: ServiceDetailUiStateAdapter,
) : ViewModel() {

    lateinit var host: String

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
                ServerDetailTab.PLACEHOLDER,
                ServerDetailTab.ABOUT,
                ServerDetailTab.TRENDS_TAG,
            )
        )
    )

    val uiState: StateFlow<ServerDetailUiState> = _uiState

    fun onPageResume() {
        launchInViewModel {
            getInstance(host)
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
