package com.zhangke.utopia.activitypub.app.internal.screen.instance.trending.tags

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.usecase.GetServerTrendTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ServerTrendsTagsViewModel @Inject constructor(
    private val getServerTrendsTags: GetServerTrendTagsUseCase,
) : ViewModel() {

    lateinit var baseUrl: FormalBaseUrl

    private val _uiState = MutableStateFlow(ServerTrendsTagsUiState(emptyList()))
    val uiState: StateFlow<ServerTrendsTagsUiState> = _uiState

    fun onPageResume() {
        launchInViewModel {
            getServerTrendsTags(baseUrl)
                .onSuccess { list ->
                    _uiState.update { it.copy(list = list) }
                }.onFailure {

                }
        }
    }
}
