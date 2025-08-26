package com.zhangke.fread.feeds.pages.manager.add.type

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.onboarding.OnboardingComponent
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.createActivityPubProtocol
import com.zhangke.fread.status.model.createBlueskyProtocol
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.tatarka.inject.annotations.Inject

class SelectContentTypeViewModel @Inject constructor(
    private val onboardingComponent: OnboardingComponent,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    private val _finishPageFlow = MutableSharedFlow<Unit>()
    val finishPageFlow = _finishPageFlow.asSharedFlow()

    init {
        launchInViewModel {
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
