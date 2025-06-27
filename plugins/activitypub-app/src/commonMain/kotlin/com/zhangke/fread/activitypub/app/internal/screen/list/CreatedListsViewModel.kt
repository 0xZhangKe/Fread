package com.zhangke.fread.activitypub.app.internal.screen.list

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.internal.usecase.content.GetUserCreatedListUseCase
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class CreatedListsViewModel @Inject constructor(
    private val getUserCreatedList: GetUserCreatedListUseCase,
    @Assisted private val locator: PlatformLocator,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(locator: PlatformLocator): CreatedListsViewModel
    }

    private val _uiState = MutableStateFlow(CreatedListsUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarFlow = MutableSharedFlow<TextString>()
    val snackBarFlow = _snackBarFlow.asSharedFlow()

    private var getListJob: Job? = null

    init {
        getUserLists()
    }

    fun onRetryClick() {
        getUserLists()
    }

    fun onPageResume() {
        getUserLists()
    }

    private fun getUserLists() {
        if (getListJob?.isActive == true) return
        _uiState.update { it.copy(loading = true) }
        getListJob = launchInViewModel {
            getUserCreatedList(locator)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(loading = false, lists = list)
                    }
                }.onFailure { t ->
                    _uiState.update {
                        it.copy(loading = false, pageError = t)
                    }
                }
        }
    }
}
