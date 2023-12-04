package com.zhangke.utopia.activitypub.app.internal.screen.server.about

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.activitypub.app.internal.usecase.account.HaveLoggedUserUseCase
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubInstanceRule
import com.zhangke.utopia.activitypub.app.internal.usecase.GetInstanceAnnouncementUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class ServerAboutViewModel @Inject constructor(
    private val haveLoggedUser: HaveLoggedUserUseCase,
    private val getInstanceAnnouncementUseCase: GetInstanceAnnouncementUseCase,
) : ViewModel() {

    lateinit var baseUrl: String

    var rules: List<ActivityPubInstanceRule> = emptyList()

    private val _uiState = MutableStateFlow(ServerAboutUiState(emptyList(), emptyList()))
    val uiState: StateFlow<ServerAboutUiState> = _uiState

    fun onPageResume() {
        _uiState.update {
            it.copy(rules = rules)
        }
        requestAnnouncement()
    }

    private fun requestAnnouncement() {
        launchInViewModel {
            if (!haveLoggedUser()) return@launchInViewModel
            getInstanceAnnouncementUseCase(baseUrl)
                .onSuccess { announcements ->
                    _uiState.update {
                        it.copy(announcement = announcements)
                    }
                }
        }
    }
}
