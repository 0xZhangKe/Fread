package com.zhangke.utopia.activitypubapp.screen.server.about

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.activitypubapp.client.ObtainActivityPubClientUseCase
import com.zhangke.utopia.activitypubapp.model.ActivityPubInstanceRule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
internal class ServerAboutViewModel(
    private val obtainActivityPubClientUseCase: ObtainActivityPubClientUseCase,
) : ViewModel() {

    lateinit var host: String

    var rules: List<ActivityPubInstanceRule> = emptyList()

    private val _uiState = MutableStateFlow(ServerAboutUiState(emptyList(), emptyList()))
    val uiState: StateFlow<ServerAboutUiState> = _uiState

    fun onPageResume() {
        _uiState.update {
            it.copy()
        }
        launchInViewModel {
            val client = obtainActivityPubClientUseCase(host)
            client.instanceRepo.getAnnouncement()
                .onSuccess {

                }.onFailure {

                }
        }
    }
}
