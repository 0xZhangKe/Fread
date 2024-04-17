package com.zhangke.utopia.activitypub.app.internal.screen.instance.tags

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.usecase.GetServerTrendTagsUseCase
import com.zhangke.utopia.status.model.IdentityRole
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
            getServerTrendsTags(IdentityRole(null, baseUrl))
                .onSuccess { list ->
                    _uiState.update { it.copy(list = list) }
                }.onFailure {

                }
        }
    }
}
