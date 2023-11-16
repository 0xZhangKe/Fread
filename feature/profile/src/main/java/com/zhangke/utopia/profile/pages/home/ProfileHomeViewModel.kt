package com.zhangke.utopia.profile.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileHomeViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileHomeUiState(emptyList()))
    val uiState: StateFlow<ProfileHomeUiState> get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            statusProvider.accountManager
                .getAllLoggedAccount()
                .onSuccess { list ->
                    list.groupBy { it.platform }
                        .map { it.key to it.value }
                        .let { dataList ->
                            _uiState.update {
                                it.copy(accountDataList = dataList)
                            }
                        }
                }
        }
    }
}
