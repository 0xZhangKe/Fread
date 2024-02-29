package com.zhangke.utopia.activitypub.app.internal.screen.content.edit

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
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
            val config = contentConfigRepo.getConfigById(configId)
            if (config == null) {
                _snackbarMessageFlow.emit(textOf(R.string.activity_pub_edit_content_screen_config_not_found))
                return@launchInViewModel
            }
            _uiState.value = EditContentConfigUiState(config)
        }
    }
}
