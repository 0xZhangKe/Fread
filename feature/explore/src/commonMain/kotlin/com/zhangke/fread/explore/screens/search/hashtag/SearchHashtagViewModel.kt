package com.zhangke.fread.explore.screens.search.hashtag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

open class SearchHashtagViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    @Assisted private val role: IdentityRole,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {
        fun create(role: IdentityRole): SearchHashtagViewModel
    }

    private val _snackMessageFlow = MutableSharedFlow<TextString>()
    val snackMessageFlow: SharedFlow<TextString> get() = _snackMessageFlow

    private val loadableController = CommonLoadableController<Hashtag>(
        viewModelScope,
        onPostSnackMessage = {
            launchInViewModel {
                _snackMessageFlow.emit(it)
            }
        },
    )

    val uiState: StateFlow<CommonLoadableUiState<Hashtag>> get() = loadableController.uiState

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    fun initQuery(query: String) {
        if (uiState.value.dataList.isNotEmpty()) return
        onRefresh(query)
    }

    fun onRefresh(query: String) {
        loadableController.onRefresh {
            statusProvider.searchEngine.searchHashtag(role, query, null)
        }
    }

    fun onLoadMore(query: String) {
        val offset = uiState.value.dataList.size
        if (offset == 0) return
        loadableController.onLoadMore {
            statusProvider.searchEngine.searchHashtag(role, query, offset)
        }
    }

    fun onHashtagClick(hashtag: Hashtag) {
        launchInViewModel {
            val screen = statusProvider.screenProvider.getTagTimelineScreen(
                role,
                hashtag.name,
                hashtag.protocol
            ) ?: return@launchInViewModel
            _openScreenFlow.emit(screen)
        }
    }
}
