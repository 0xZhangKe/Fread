package com.zhangke.utopia.feeds.pages.manager.selecttype

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.ContentType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class SelectContentTypeViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val contentConfigRepo: ContentConfigRepo,
) : ViewModel() {

    private val _openPageFlow = MutableSharedFlow<String>()
    val openPageFlow: SharedFlow<String> get() = _openPageFlow

    private val _addContentSuccessFlow = MutableSharedFlow<Unit>()
    val addContentSuccessFlow: SharedFlow<Unit> get() = _addContentSuccessFlow

    fun onTypeClick(type: ContentType) {
        launchInViewModel {
            statusProvider.screenProvider
                .getAddContentScreenRoute(type)
                ?.let { _openPageFlow.emit(it) }
        }
    }

    fun onConfigAdd(contentConfig: ContentConfig) {
        launchInViewModel {
            contentConfigRepo.insert(contentConfig)
            _addContentSuccessFlow.emit(Unit)
        }
    }
}
