package com.zhangke.fread.activitypub.app.internal.screen.filters.list

import androidx.lifecycle.ViewModel
import com.zhangke.activitypub.entities.ActivityPubFilterEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.date.DateParser
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_filters_active
import com.zhangke.fread.activitypub.app.activity_pub_filters_expired
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.ext.getCurrentTimeMillis
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class FiltersListViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    @Assisted private val role: IdentityRole,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(role: IdentityRole): FiltersListViewModel
    }

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
        return clientManager.getClient(role)
            .accountRepo
            .getFilters()
            .map { list -> list.map { it.toUiState() } }
    }

    private fun ActivityPubFilterEntity.toUiState(): FilterItemUiState {
        val expiresAtDate = this.expiresAt?.let(DateParser::parseAll)
        val validateDescription = if (expiresAtDate != null && expiresAtDate.toEpochMilliseconds() < getCurrentTimeMillis()){
            textOf(Res.string.activity_pub_filters_expired)
        }else{
            textOf(Res.string.activity_pub_filters_active)
        }
        return FilterItemUiState(
            id = this.id,
            title = this.title,
            validateDescription = validateDescription,
        )
    }
}
