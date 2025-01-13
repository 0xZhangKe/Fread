package com.zhangke.fread.profile.screen.setting.about

import com.zhangke.fread.common.update.AppReleaseInfo

data class AboutUiState(
    val checkingUpdate: Boolean,
    val newReleaseInfo: AppReleaseInfo?,
) {

    companion object {

        fun default() = AboutUiState(
            checkingUpdate = false,
            newReleaseInfo = null,
        )
    }

    sealed interface UpdatingState {

        data object Idle : UpdatingState

        data object Checking : UpdatingState

        data class Failed(val throwable: Throwable) : UpdatingState

        data object DoNotNeedUpdate : UpdatingState

        data class NeedUpdate(val appReleaseInfo: AppReleaseInfo) : UpdatingState
    }
}
