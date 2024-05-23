package com.zhangke.utopia.screen.main.drawer

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.ktx.launchInScreenModel
import com.zhangke.krouter.KRouter
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.feeds.pages.manager.edit.EditMixedContentScreen
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.ContentConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class MainDrawerScreenModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val statusProvider: StatusProvider,
) : ScreenModel {

    private val _uiState = MutableStateFlow(MainDrawerUiState(emptyList()))
    val uiState: StateFlow<MainDrawerUiState> get() = _uiState

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    init {
        launchInScreenModel {
            contentConfigRepo.getAllConfigFlow()
                .collect { list ->
                    _uiState.update { it.copy(contentConfigList = list) }
                }
        }
    }

    fun onContentConfigMove(from: Int, to: Int) {
        launchInScreenModel {
            val configList = _uiState.value.contentConfigList
            if (configList.isEmpty()) return@launchInScreenModel
            contentConfigRepo.reorderConfig(configList[from], configList[to])
        }
    }

    fun onContentConfigEditClick(contentConfig: ContentConfig) {
        launchInScreenModel {
            when (contentConfig) {
                is ContentConfig.MixedContent -> {
                    _openScreenFlow.emit(EditMixedContentScreen(contentConfig.id))
                }

                is ContentConfig.ActivityPubContent -> {
                    statusProvider.screenProvider.getEditContentConfigScreenRoute(contentConfig)
                        ?.let { KRouter.route<Screen>(it) }
                        ?.let { _openScreenFlow.emit(it) }
                }
            }
        }
    }
}
