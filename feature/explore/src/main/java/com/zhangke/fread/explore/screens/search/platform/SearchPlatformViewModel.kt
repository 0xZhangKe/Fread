package com.zhangke.fread.explore.screens.search.platform

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.platform.BlogPlatform
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
open class SearchPlatformViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _snackMessageFlow = MutableSharedFlow<TextString>()
    val snackMessageFlow: SharedFlow<TextString> get() = _snackMessageFlow

    private val loadableController = CommonLoadableController<BlogPlatform>(
        viewModelScope,
        onPostSnackMessage = {
            launchInViewModel {
                _snackMessageFlow.emit(it)
            }
        },
    )

    val uiState: StateFlow<CommonLoadableUiState<BlogPlatform>> get() = loadableController.uiState

    fun initQuery(query: String) {
        if (uiState.value.dataList.isNotEmpty()) return
        onRefresh(query)
    }

    fun onRefresh(query: String) {
        loadableController.onRefresh {
            statusProvider.searchEngine.searchPlatform(query, null)
        }
    }

    fun onLoadMore(query: String) {
        val offset = uiState.value.dataList.size
        if (offset == 0) return
        loadableController.onLoadMore {
            statusProvider.searchEngine.searchPlatform(query, offset)
        }
    }
}
