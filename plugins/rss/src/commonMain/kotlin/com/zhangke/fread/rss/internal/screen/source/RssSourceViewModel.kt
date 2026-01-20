package com.zhangke.fread.rss.internal.screen.source

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.utils.formatDefault
import com.zhangke.fread.rss.internal.repo.RssRepo
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
class RssSourceViewModel(
    private val rssRepo: RssRepo,
    private val url: String,
) : ViewModel() {

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
                            formattedAddDate = it.addDate.formatDefault(),
                            formattedLastUpdateDate = it.lastUpdateDate.formatDefault(),
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
