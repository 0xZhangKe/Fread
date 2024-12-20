package com.zhangke.fread.screen.main.drawer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.common.routeScreen
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import com.zhangke.fread.feeds.pages.manager.edit.EditMixedContentScreen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.krouter.KRouter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class MainDrawerViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainDrawerUiState(emptyList()))
    val uiState: StateFlow<MainDrawerUiState> get() = _uiState

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            contentConfigRepo.getAllConfigFlow()
                .collect { list ->
                    _uiState.update { it.copy(contentConfigList = list) }
                }
        }
    }

    fun onContentConfigMove(from: Int, to: Int) {
        viewModelScope.launch {
            val configList = _uiState.value.contentConfigList
            if (configList.isEmpty()) return@launch
            contentConfigRepo.reorderConfig(configList[from], configList[to])
        }
    }

    fun onContentConfigEditClick(contentConfig: ContentConfig) {
        viewModelScope.launch {
            when (contentConfig) {
                is ContentConfig.MixedContent -> {
                    _openScreenFlow.emit(EditMixedContentScreen(contentConfig.id))
                }

                is ContentConfig.ActivityPubContent -> {
                    statusProvider.screenProvider.getEditContentConfigScreenRoute(contentConfig)
                        ?.let { KRouter.routeScreen(it) }
                        ?.let { _openScreenFlow.emit(it) }
                }
            }
        }
    }
}
