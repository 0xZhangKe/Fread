package com.zhangke.fread.profile.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.ai.LLMModelConfigsRepo
import com.zhangke.fread.common.handler.TextHandler
import com.zhangke.fread.common.update.AppUpdateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingScreenModel(
    private val textHandler: TextHandler,
    private val updateManager: AppUpdateManager,
    private val modelConfigRepo: LLMModelConfigsRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingUiState(
            settingInfo = getAppVersionInfo(),
            haveNewAppVersion = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            if (updateManager.enableAutoCheckUpdate) {
                updateManager.checkForUpdate(false)
                    .onSuccess { (needUpdate, _) ->
                        _uiState.update { it.copy(haveNewAppVersion = needUpdate) }
                    }
            }
        }
        launchInViewModel {
            modelConfigRepo.getAllProviderFlow().collect { models ->
                _uiState.update {
                    it.copy(currentLLMModel = models.firstOrNull { model -> model.selected })
                }
            }
        }
    }

    private fun getAppVersionInfo(): String {
        return "${textHandler.versionName}(${textHandler.versionCode})"
    }
}
