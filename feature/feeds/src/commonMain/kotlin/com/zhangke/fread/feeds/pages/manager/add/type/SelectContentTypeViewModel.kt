package com.zhangke.fread.feeds.pages.manager.add.type

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.onboarding.OnboardingComponent
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.createActivityPubProtocol
import com.zhangke.fread.status.model.createBlueskyProtocol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SelectContentTypeViewModel(
    private val onboardingComponent: OnboardingComponent,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _openScreenFlow = MutableSharedFlow<NavKey>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    private val _finishPageFlow = MutableSharedFlow<Unit>()
    val finishPageFlow = _finishPageFlow.asSharedFlow()

    init {
        onboardingComponent.clearState()
    }

    fun onPageResumed(uiScope: CoroutineScope) {
        uiScope.launch {
            onboardingComponent.onboardingFinishedFlow.collect {
                _finishPageFlow.emit(Unit)
            }
        }
    }

    fun onMastodonClick() {
        statusProvider.screenProvider
            .getAddContentScreen(createActivityPubProtocol())
            .let {
                launchInViewModel { _openScreenFlow.emit(it) }
            }
    }

    fun onBlueskyClick() {
        statusProvider.screenProvider
            .getAddContentScreen(createBlueskyProtocol())
            .let { launchInViewModel { _openScreenFlow.emit(it) } }
    }
}
