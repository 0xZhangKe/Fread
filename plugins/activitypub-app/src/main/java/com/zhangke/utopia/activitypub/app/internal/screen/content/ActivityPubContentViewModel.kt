package com.zhangke.utopia.activitypub.app.internal.screen.content

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.composable.updateToLoading
import com.zhangke.framework.composable.updateToSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.status.model.ContentConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ActivityPubContentViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
) : ViewModel() {

    var configId: Long = 0L

    private val _uiState =
        MutableStateFlow<LoadableState<ActivityPubContentUiState>>(LoadableState.idle())
    val uiState = _uiState.asStateFlow()

    fun onPrepared() {
        if (_uiState.value.isLoading) return
        launchInViewModel {
            _uiState.updateToLoading()
            val contentConfig =
                contentConfigRepo.getConfigById(configId) as? ContentConfig.ActivityPubContent
            if (contentConfig != null) {
                _uiState.updateToSuccess(ActivityPubContentUiState(contentConfig))
            } else {
                _uiState.updateToFailed(IllegalArgumentException("Cant find validate config by id: $configId"))
            }
        }
    }
}
