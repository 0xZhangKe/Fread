package com.zhangke.fread.screen.main

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.update.AppReleaseInfo
import com.zhangke.fread.common.update.AppUpdateManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class MainViewModel @Inject constructor(
    private val updateManager: AppUpdateManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainPageUiState(null))
    val uiState = _uiState.asStateFlow()

    init {
        launchInViewModel {
            if (updateManager.enableAutoCheckUpdate) {
                delay(5000)
                updateManager.checkForUpdate()
                    .onSuccess { (needUpdate, info) ->
                        if (needUpdate) {
                            _uiState.update { it.copy(newAppReleaseInfo = info) }
                        }
                    }
            }
        }
    }

    fun onCancelClick(releaseInfo: AppReleaseInfo) {
        _uiState.update { it.copy(newAppReleaseInfo = null) }
        launchInViewModel {
            updateManager.ignoreVersion(releaseInfo)
        }
    }

    fun onUpdateClick(releaseInfo: AppReleaseInfo) {
        _uiState.update { it.copy(newAppReleaseInfo = null) }
        launchInViewModel {
            updateManager.updateApp(releaseInfo)
        }
    }
}
