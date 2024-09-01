package com.zhangke.fread.activitypub.app.internal.screen.instance.about

import androidx.lifecycle.ViewModel
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.repo.account.ActivityPubLoggedAccountRepo
import com.zhangke.fread.activitypub.app.internal.usecase.GetInstanceAnnouncementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class ServerAboutViewModel @Inject constructor(
    private val accountRepo: ActivityPubLoggedAccountRepo,
    private val getInstanceAnnouncementUseCase: GetInstanceAnnouncementUseCase,
) : ViewModel() {

    lateinit var baseUrl: FormalBaseUrl

    var rules: List<ActivityPubInstanceEntity.Rule> = emptyList()

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
            if (accountRepo.queryAll().isEmpty()) return@launchInViewModel
            getInstanceAnnouncementUseCase(baseUrl)
                .onSuccess { announcements ->
                    _uiState.update {
                        it.copy(announcement = announcements)
                    }
                }
        }
    }
}
