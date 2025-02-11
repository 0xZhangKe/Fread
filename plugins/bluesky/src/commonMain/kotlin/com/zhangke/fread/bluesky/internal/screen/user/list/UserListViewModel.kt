package com.zhangke.fread.bluesky.internal.screen.user.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.controller.CommonLoadableController
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class UserListViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    @Assisted private val role: IdentityRole,
    @Assisted private val type: UserListType,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
            type: UserListType,
        ): UserListViewModel
    }

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage


    private val loadController = CommonLoadableController<BlogAuthor>(
        viewModelScope,
        onPostSnackMessage = {
            launchInViewModel { _snackBarMessage.emit(it) }
        },
    )

    val uiState: StateFlow<CommonLoadableUiState<BlogAuthor>> get() = loadController.uiState

    private var cursor: String? = null

    init {
        loadController.initData(
            getDataFromLocal = { emptyList() },
            getDataFromServer = ::getDataFromServer,
        )
    }

    fun onRefresh() {
        loadController.onRefresh { getDataFromServer(null) }
    }

    fun onLoadMore() {
        loadController.onLoadMore { getDataFromServer() }
    }

    private suspend fun getDataFromServer(cursor: String? = null): Result<List<BlogAuthor>> {
        return Result.success(emptyList())
    }
}
