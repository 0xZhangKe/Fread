package com.zhangke.utopia.activitypub.app.internal.screen.instance

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubInstanceAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.platform.BlogPlatform
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class InstanceDetailViewModel @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val platformRepo: ActivityPubPlatformRepo,
    private val instanceAdapter: ActivityPubInstanceAdapter,
) : ViewModel() {

    lateinit var serverBaseUrl: FormalBaseUrl
    var addable: Boolean = false

    private val _uiState = MutableStateFlow(
        InstanceDetailUiState(
            loading = false,
            baseUrl = null,
            instance = null,
            addable = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _openLoginFlow = MutableSharedFlow<List<BlogPlatform>>()
    val openLoginFlow: SharedFlow<List<BlogPlatform>> get() = _openLoginFlow

    private val _contentConfigFlow = MutableSharedFlow<ContentConfig>()
    val contentConfigFlow: SharedFlow<ContentConfig> get() = _contentConfigFlow

    fun onPrepared() {
        _uiState.value = _uiState.value.copy(
            loading = true,
            addable = addable,
            baseUrl = serverBaseUrl
        )
        launchInViewModel {
            val platform = platformRepo.getInstanceEntity(serverBaseUrl).getOrNull()
            _uiState.value = _uiState.value.copy(
                loading = false,
                instance = platform,
            )
        }
    }

    fun onAddClick() {
        val instance = _uiState.value.instance ?: return
        launchInViewModel {
            if (accountManager.getAllLoggedAccount().isEmpty()) {
                _openLoginFlow.emit(listOf(instanceAdapter.toPlatform(instance)))
            } else {
                emitCurrentContentConfigFlow()
            }
        }
    }

    private suspend fun emitCurrentContentConfigFlow() {
        val instance = _uiState.value.instance ?: return
        val config = ContentConfig.ActivityPubContent(
            id = 0,
            name = instance.title,
            baseUrl = serverBaseUrl,
        )
        _contentConfigFlow.emit(config)
    }
}
