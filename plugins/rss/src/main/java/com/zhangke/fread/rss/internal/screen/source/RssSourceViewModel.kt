package com.zhangke.fread.rss.internal.screen.source

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.rss.internal.repo.RssRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.DateFormat
import java.util.Locale

@HiltViewModel(assistedFactory = RssSourceViewModel.Factory::class)
class RssSourceViewModel @AssistedInject constructor(
    private val rssRepo: RssRepo,
    @Assisted private val url: String,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(url: String): RssSourceViewModel
    }

    private val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())

    private val _uiState = MutableStateFlow(
        RssSourceUiState(
            source = null,
            formattedAddDate = null,
            formattedLastUpdateDate = null,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    init {
        launchInViewModel {
            rssRepo.getRssSource(url)
                .onFailure {
                    it.message
                        ?.let { textOf(it) }
                        ?.let { _snackBarMessageFlow.emit(it) }
                }.onSuccess {
                    if (it == null) {
                        _snackBarMessageFlow.emit(textOf("Unknown $url"))
                    } else {
                        _uiState.value = _uiState.value.copy(
                            source = it,
                            formattedAddDate = dateFormat.format(it.addDate),
                            formattedLastUpdateDate = dateFormat.format(it.lastUpdateDate),
                        )
                    }
                }
        }
    }

    fun onDisplayNameChanged(displayName: String) {
        _uiState.value = _uiState.value.copy(
            source = _uiState.value.source?.copy(
                displayName = displayName
            )
        )
        launchInViewModel {
            rssRepo.updateSourceName(url, displayName)
        }
    }

}
