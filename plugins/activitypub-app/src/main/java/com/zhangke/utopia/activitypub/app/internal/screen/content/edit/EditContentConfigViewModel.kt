package com.zhangke.utopia.activitypub.app.internal.screen.content.edit

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.status.model.ContentConfig
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = EditContentConfigViewModel.Factory::class)
class EditContentConfigViewModel @AssistedInject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    @Assisted private val configId: Long
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(configId: Long): EditContentConfigViewModel
    }

    private val _uiState = MutableStateFlow<EditContentConfigUiState?>(null)
    val uiState = _uiState.asStateFlow()
    private val _snackbarMessageFlow = MutableSharedFlow<TextString>()
    val snackbarMessageFlow = _snackbarMessageFlow.asSharedFlow()

    init {
        launchInViewModel {
            contentConfigRepo.getConfigFlowById(configId)
                .collect { config ->
                    if (config !is ContentConfig.ActivityPubContent) {
                        _snackbarMessageFlow.emit(textOf(R.string.activity_pub_edit_content_screen_config_not_found))
                        return@collect
                    }
                    _uiState.value = EditContentConfigUiState(config)
                }
        }
    }

    fun onShowingTabMove(from: Int, to: Int) {
        val uiState = _uiState.value ?: return
        val config = uiState.config
        val showingTabList = config.showingTabList
        launchInViewModel {
            contentConfigRepo.recorderActivityPubShowingTab(
                configId = config.id,
                fromTab = showingTabList[from],
                toTab = showingTabList[to],
            )
        }
    }

    fun onShowingTabMoveDown(tab: ContentConfig.ActivityPubContent.ContentTab) {

    }

    fun onHiddenTabMoveUp(tab: ContentConfig.ActivityPubContent.ContentTab) {

    }
}
