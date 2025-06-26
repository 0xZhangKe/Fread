package com.zhangke.fread.activitypub.app.internal.screen.instance.tags

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.usecase.GetServerTrendTagsUseCase
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class ServerTrendsTagsViewModel @Inject constructor(
    private val getServerTrendsTags: GetServerTrendTagsUseCase,
) : ViewModel() {

    lateinit var baseUrl: FormalBaseUrl

    private val _uiState = MutableStateFlow(ServerTrendsTagsUiState(emptyList()))
    val uiState: StateFlow<ServerTrendsTagsUiState> = _uiState

    fun onPageResume() {
        launchInViewModel {
            getServerTrendsTags(PlatformLocator(accountUri = null, baseUrl = baseUrl))
                .onSuccess { list ->
                    _uiState.update { it.copy(list = list) }
                }.onFailure {

                }
        }
    }
}
