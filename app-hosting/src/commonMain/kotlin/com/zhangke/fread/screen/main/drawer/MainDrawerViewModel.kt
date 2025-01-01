package com.zhangke.fread.screen.main.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.feeds.pages.manager.edit.EditMixedContentScreen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.content.MixedContent
import com.zhangke.fread.status.model.FreadContent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class MainDrawerViewModel @Inject constructor(
    private val contentRepo: FreadContentRepo,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainDrawerUiState(emptyList()))
    val uiState: StateFlow<MainDrawerUiState> get() = _uiState

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            contentRepo.getAllContentFlow()
                .collect { list ->
                    _uiState.update { it.copy(contentConfigList = list) }
                }
        }
    }

    fun onContentConfigMove(from: Int, to: Int) {
        viewModelScope.launch {
            val configList = _uiState.value.contentConfigList
            if (configList.isEmpty()) return@launch
            contentRepo.reorderConfig(configList[from], configList[to])
        }
    }

    fun onContentConfigEditClick(content: FreadContent) {
        viewModelScope.launch {
            if (content is MixedContent) {
                _openScreenFlow.emit(EditMixedContentScreen(content.id))
            } else {
                statusProvider.screenProvider.getEditContentConfigScreenScreen(content)
                    ?.let { _openScreenFlow.emit(it) }
            }
        }
    }
}
