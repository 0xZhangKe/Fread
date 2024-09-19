package com.zhangke.fread.activitypub.app.internal.screen.instance

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.di.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class InstanceDetailViewModel @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
    private val authorAdapter: ActivityPubAccountEntityAdapter,
    @Assisted private val serverBaseUrl: FormalBaseUrl,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

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
