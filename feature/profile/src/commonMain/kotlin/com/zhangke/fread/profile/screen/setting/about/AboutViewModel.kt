package com.zhangke.fread.profile.screen.setting.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.fread.common.update.AppUpdateManager
import com.zhangke.fread.feature.profile.Res
import com.zhangke.fread.feature.profile.profile_setting_already_latest_version
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class AboutViewModel @Inject constructor(
    private val updateManager: AppUpdateManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AboutUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    init {
        onCheckForUpdateClick(false)
    }

    fun onCheckForUpdateClick(showLoading: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(checkingUpdate = showLoading, newReleaseInfo = null) }
            updateManager.checkForUpdate(false)
                .onFailure { t ->
                    _uiState.update { it.copy(checkingUpdate = false) }
                    _snackBarMessage.emitTextMessageFromThrowable(t)
                }.onSuccess { (needUpdate, releaseInfo) ->
                    _uiState.update {
                        it.copy(
                            checkingUpdate = false,
                            newReleaseInfo = if (needUpdate) releaseInfo else null,
                        )
                    }
                    if (!needUpdate) {
                        _snackBarMessage.emit(textOf(Res.string.profile_setting_already_latest_version))
                    }
                }
        }
    }

    fun onUpdateClick() {
        viewModelScope.launch {
            _uiState.value.newReleaseInfo?.let {
                updateManager.updateApp(it)
            }
        }
    }

    fun onCancelClick() {
        viewModelScope.launch {
            _uiState.value.newReleaseInfo?.let {
                updateManager.ignoreVersion(it)
            }
        }
    }
}
