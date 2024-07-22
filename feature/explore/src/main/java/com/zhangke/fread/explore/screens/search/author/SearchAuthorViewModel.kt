package com.zhangke.fread.explore.screens.search.author

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel(assistedFactory = SearchAuthorViewModel.Factory::class)
open class SearchAuthorViewModel @AssistedInject constructor(
    private val statusProvider: StatusProvider,
    @Assisted val role: IdentityRole,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(role: IdentityRole): SearchAuthorViewModel
    }

    private val _snackMessageFlow = MutableSharedFlow<TextString>()
    val snackMessageFlow: SharedFlow<TextString> get() = _snackMessageFlow

    private val loadableController = CommonLoadableController<BlogAuthor>(
        viewModelScope,
        onPostSnackMessage = {
            launchInViewModel {
                _snackMessageFlow.emit(it)
            }
        })
    val uiState: StateFlow<CommonLoadableUiState<BlogAuthor>> get() = loadableController.uiState

    private val _openScreenFlow = MutableSharedFlow<Any>()
    val openScreenFlow: SharedFlow<Any> get() = _openScreenFlow

    fun initQuery(query: String) {
        if (loadableController.uiState.value.dataList.isNotEmpty()) return
        onRefresh(query)
    }

    fun onRefresh(query: String) {
        loadableController.onRefresh {
            statusProvider.searchEngine.searchAuthor(role, query, null)
        }
    }

    fun onLoadMore(query: String) {
        val offset = uiState.value.dataList.size
        if (offset == 0) return
        loadableController.onLoadMore {
            statusProvider.searchEngine.searchAuthor(role, query, offset)
        }
    }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        val route = statusProvider.screenProvider.getUserDetailRoute(role, blogAuthor.uri) ?: return
        launchInViewModel {
            _openScreenFlow.emit(route)
        }
    }
}
