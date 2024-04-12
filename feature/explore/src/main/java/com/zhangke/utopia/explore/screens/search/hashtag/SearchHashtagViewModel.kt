package com.zhangke.utopia.explore.screens.search.hashtag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.krouter.KRouter
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.Hashtag
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

@HiltViewModel(assistedFactory = SearchHashtagViewModel.Factory::class)
open class SearchHashtagViewModel @AssistedInject constructor(
    private val statusProvider: StatusProvider,
    @Assisted private val baseUrl: FormalBaseUrl,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(baseUrl: FormalBaseUrl): SearchHashtagViewModel
    }

    private val loadableController = CommonLoadableController<Hashtag>(viewModelScope)

    val uiState: StateFlow<CommonLoadableUiState<Hashtag>> get() = loadableController.uiState

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    fun onRefresh(query: String) {
        loadableController.onRefresh {
            statusProvider.searchEngine.searchHashtag(baseUrl, query, null)
        }
    }

    fun onLoadMore(query: String) {
        val offset = uiState.value.dataList.size
        if (offset == 0) return
        loadableController.onLoadMore {
            statusProvider.searchEngine.searchHashtag(baseUrl, query, offset)
        }
    }

    fun onHashtagClick(hashtag: Hashtag) {
        launchInViewModel {
            val route = statusProvider.screenProvider.getTagTimelineScreenRoute(hashtag)
                ?: return@launchInViewModel
            KRouter.route<Screen>(route)?.let {
                _openScreenFlow.emit(it)
            }
        }
    }
}
