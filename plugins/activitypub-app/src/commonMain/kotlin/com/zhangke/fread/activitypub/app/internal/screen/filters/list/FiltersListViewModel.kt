package com.zhangke.fread.activitypub.app.internal.screen.filters.list

import androidx.lifecycle.ViewModel
import com.zhangke.activitypub.entities.ActivityPubFilterEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.date.DateParser
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.utils.getCurrentTimeMillis
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
class FiltersListViewModel (
    private val clientManager: ActivityPubClientManager,
    private val locator: PlatformLocator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FiltersListUiState.default())
    val uiState: StateFlow<FiltersListUiState> = _uiState

    private val _snackBarFlow = MutableSharedFlow<TextString>()
    val snackBarFlow: SharedFlow<TextString> = _snackBarFlow

    init {
        launchInViewModel {
            _uiState.update { it.copy(initializing = true) }
            fetchFilters()
                .onSuccess { list ->
                    _uiState.update { it.copy(initializing = false, list = list) }
                }.onFailure { t ->
                    _uiState.update { it.copy(initializing = false) }
                    _snackBarFlow.emitTextMessageFromThrowable(t)
                }
        }
    }

    fun onPageResume() {
        launchInViewModel {
            fetchFilters()
                .onSuccess { list ->
                    _uiState.update { it.copy(list = list) }
                }.onFailure { t ->
                    _snackBarFlow.emitTextMessageFromThrowable(t)
                }
        }
    }

    private suspend fun fetchFilters(): Result<List<FilterItemUiState>> {
        return clientManager.getClient(locator)
            .accountRepo
            .getFilters()
            .map { list -> list.map { it.toUiState() } }
    }

    private fun ActivityPubFilterEntity.toUiState(): FilterItemUiState {
        val expiresAtDate = this.expiresAt?.let(DateParser::parseAll)
        val validateDescription = if (expiresAtDate != null && expiresAtDate.toEpochMilliseconds() < getCurrentTimeMillis()){
            textOf(LocalizedString.activity_pub_filters_expired)
        }else{
            textOf(LocalizedString.activity_pub_filters_active)
        }
        return FilterItemUiState(
            id = this.id,
            title = this.title,
            validateDescription = validateDescription,
        )
    }
}