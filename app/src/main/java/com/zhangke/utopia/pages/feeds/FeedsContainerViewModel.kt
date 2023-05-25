package com.zhangke.utopia.pages.feeds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.db.ChannelRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedsContainerViewModel @Inject constructor(
    private val channelRepo: ChannelRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState())
    val uiState: StateFlow<FeedsContainerUiState> = _uiState.asStateFlow()

    init {

    }

    private fun loadTabs() {
        viewModelScope.launch {
            val s = channelRepo.queryAll()
        }
    }

    fun onPageChanged(index: Int) {
        _uiState.update {
            it.copy(tabIndex = index)
        }
    }

    private fun initialState(): FeedsContainerUiState {
        return FeedsContainerUiState(
            channelList = LoadableState.loading(),
            tabIndex = -1,
        )
    }
}