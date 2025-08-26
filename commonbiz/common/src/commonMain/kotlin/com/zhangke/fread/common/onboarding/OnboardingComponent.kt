package com.zhangke.fread.common.onboarding

import com.zhangke.framework.utils.Log
import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class OnboardingComponent @Inject constructor() {

    private val _onboardingFinishedFlow = MutableSharedFlow<Unit>(1)
    val onboardingFinishedFlow = _onboardingFinishedFlow.asSharedFlow()

    suspend fun onboardingSuccess() {
        _onboardingFinishedFlow.emit(Unit)
        Log.d("F_TEST") { "onboardingSuccess" }
    }

    fun clearState() {
        _onboardingFinishedFlow.resetReplayCache()
        Log.d("F_TEST") { "reset state" }
    }
}
