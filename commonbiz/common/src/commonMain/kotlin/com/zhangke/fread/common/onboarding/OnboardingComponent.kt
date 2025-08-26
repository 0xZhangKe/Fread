package com.zhangke.fread.common.onboarding

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.tatarka.inject.annotations.Inject

class OnboardingComponent @Inject constructor() {

    private val _onboardingFinishedFlow = MutableSharedFlow<Unit>()
    val onboardingFinishedFlow = _onboardingFinishedFlow.asSharedFlow()

    suspend fun onboardingSuccess() {
        _onboardingFinishedFlow.emit(Unit)
    }
}
