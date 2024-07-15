package com.zhangke.fread.activitypub.app.internal.screen.instance

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubLoggedAccountAdapter
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = InstanceDetailViewModel.Factory::class)
class InstanceDetailViewModel @AssistedInject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
    private val authorAdapter: ActivityPubAccountEntityAdapter,
    @Assisted private val serverBaseUrl: FormalBaseUrl,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {

        fun create(serverBaseUrl: FormalBaseUrl): InstanceDetailViewModel
    }

    private val _uiState = MutableStateFlow(
        InstanceDetailUiState(
            loading = false,
            baseUrl = null,
            instance = null,
            modAccount = null,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            loading = true,
            baseUrl = serverBaseUrl
        )
        launchInViewModel {
            val platform = platformRepo.getInstanceEntity(serverBaseUrl).getOrNull()
            _uiState.value = _uiState.value.copy(
                loading = false,
                instance = platform,
                modAccount = platform?.contact?.account?.let { authorAdapter.toAuthor(it) }
            )
        }
    }
}
