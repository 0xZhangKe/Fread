package com.zhangke.fread.explore.screens.search.hashtag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.routeScreen
import com.zhangke.krouter.KRouter
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

@HiltViewModel(assistedFactory = SearchHashtagViewModel.Factory::class)
open class SearchHashtagViewModel @AssistedInject constructor(
    private val statusProvider: StatusProvider,
    @Assisted private val role: IdentityRole,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
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
            val route = statusProvider.screenProvider.getTagTimelineScreenRoute(
                role,
                hashtag.name,
                hashtag.protocol
            )
                ?: return@launchInViewModel
            KRouter.routeScreen(route)?.let {
                _openScreenFlow.emit(it)
            }
        }
    }
}
