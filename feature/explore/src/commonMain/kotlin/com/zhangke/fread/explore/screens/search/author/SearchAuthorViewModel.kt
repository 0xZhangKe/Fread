package com.zhangke.fread.explore.screens.search.author

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

open class SearchAuthorViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    @Assisted val role: IdentityRole,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {
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
        launchInViewModel {
            statusProvider.screenProvider
                .getUserDetailRoute(role, blogAuthor.uri)
                ?.let { _openScreenFlow.emit(it) }
        }
    }
}
