package com.zhangke.fread.activitypub.app.internal.screen.instance

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InstanceDetailViewModel (
    private val platformRepo: ActivityPubPlatformRepo,
    private val authorAdapter: ActivityPubAccountEntityAdapter,
    private val serverBaseUrl: FormalBaseUrl,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        InstanceDetailUiState(
            loading = false,
            baseUrl = serverBaseUrl,
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
