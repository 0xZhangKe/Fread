package com.zhangke.fread.screen.main.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.feeds.pages.manager.edit.EditMixedContentScreenNavKey
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.content.MixedContent
import com.zhangke.fread.status.model.FreadContent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainDrawerViewModel (
    private val contentRepo: FreadContentRepo,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainDrawerUiState(emptyList()))
    val uiState: StateFlow<MainDrawerUiState> get() = _uiState

    private val _openScreenFlow = MutableSharedFlow<NavKey>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            contentRepo.getAllContentFlow()
                .collect { list ->
                    val contentList = mapToMainDrawerContent(list)
                    _uiState.update { it.copy(contentConfigList = contentList) }
                }
        }
    }

    private suspend fun mapToMainDrawerContent(
        contentList: List<FreadContent>,
    ): List<MainDrawerContent> {
        val allAccounts = statusProvider.accountManager.getAllLoggedAccount()
        return contentList.map { content ->
            val account = allAccounts.find { it.uri == content.accountUri }
            MainDrawerContent(content, account)
        }
    }

    fun onContentConfigMove(from: Int, to: Int) {
        viewModelScope.launch {
            val configList = _uiState.value.contentConfigList
            if (configList.isEmpty()) return@launch
            contentRepo.reorderConfig(configList[from].content, configList[to].content)
        }
    }

    fun onContentConfigEditClick(content: FreadContent) {
        viewModelScope.launch {
            if (content is MixedContent) {
                _openScreenFlow.emit(EditMixedContentScreenNavKey(content.id))
            } else {
                statusProvider.screenProvider.getEditContentConfigScreenScreen(content)
                    ?.let { _openScreenFlow.emit(it) }
            }
        }
    }
}