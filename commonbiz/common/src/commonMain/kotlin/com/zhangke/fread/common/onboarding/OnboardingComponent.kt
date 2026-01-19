package com.zhangke.fread.common.onboarding

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class OnboardingComponent () {

    private val _onboardingFinishedFlow = MutableSharedFlow<Unit>(1)
    val onboardingFinishedFlow = _onboardingFinishedFlow.asSharedFlow()

    suspend fun onboardingSuccess() {
        _onboardingFinishedFlow.emit(Unit)
    }

    fun clearState() {
        _onboardingFinishedFlow.resetReplayCache()
    }
}