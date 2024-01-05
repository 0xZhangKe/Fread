package com.zhangke.utopia.feeds.pages.manager.selecttype

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.StatusProviderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class SelectFeedsTypeViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _openPageFlow = MutableSharedFlow<Any>()
    val openPageFlow: SharedFlow<Any> get() = _openPageFlow

    fun onTypeClick(type: StatusProviderType) {
        launchInViewModel {
            statusProvider.screenProvider
                .getAddContentScreen(type)
                ?.let { _openPageFlow.emit(it) }
        }
    }
}
