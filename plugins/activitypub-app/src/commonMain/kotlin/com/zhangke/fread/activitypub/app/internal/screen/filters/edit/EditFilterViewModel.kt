package com.zhangke.fread.activitypub.app.internal.screen.filters.edit

import androidx.lifecycle.ViewModel
import com.zhangke.activitypub.entities.ActivityPubCreateFilterEntity
import com.zhangke.activitypub.entities.ActivityPubFilterEntity
import com.zhangke.activitypub.entities.ActivityPubFilterKeywordEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.date.DateParser
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.utils.getCurrentTimeMillis
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant

@OptIn(ExperimentalTime::class) class EditFilterViewModel (
    private val clientManager: ActivityPubClientManager,
    private val locator: PlatformLocator,
    private val id: String?,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditFilterUiState.default())
    val uiState: StateFlow<EditFilterUiState> = _uiState

    private val _snackBarFlow = MutableSharedFlow<TextString>()
    val snackBarFlow: SharedFlow<TextString> = _snackBarFlow

    private val _finishPageFlow = MutableSharedFlow<Unit>()
    val finishPageFlow: SharedFlow<Unit> = _finishPageFlow

    private var submitJob: Job? = null

    init {
        loadFilter()
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title, hasInputtedSomething = true) }
    }

    @OptIn(ExperimentalTime::class)
    fun onExpiredDateSelected(date: Instant?) {
        _uiState.update { it.copy(expiresDate = date, hasInputtedSomething = true) }
    }

    fun onKeywordChanged(keywordList: List<EditFilterUiState.Keyword>) {
        _uiState.update { it.copy(keywordList = keywordList, hasInputtedSomething = true) }
    }

    fun onContextChanged(contextList: List<FilterContext>) {
        _uiState.update { it.copy(contextList = contextList, hasInputtedSomething = true) }
    }

    fun onWarningCheckChanged(checked: Boolean) {
        _uiState.update { it.copy(filterByWarn = checked, hasInputtedSomething = true) }
    }

    fun onDeleteClick() {
        val filterId = id ?: return
        launchInViewModel {
            clientManager.getClient(locator).accountRepo
                .deleteFilter(filterId)
                .onSuccess {
                    _finishPageFlow.emit(Unit)
                }.onFailure {
                    _snackBarFlow.emitTextMessageFromThrowable(it)
                }
        }
    }

    fun onSubmitClick() {
        if (submitJob?.isActive == true) return
        submitJob = launchInViewModel {
            val request = _uiState.value.toRequest()
            val accountRepo = clientManager.getClient(locator).accountRepo
            if (id.isNullOrEmpty()) {
                accountRepo.createFilters(request)
            } else {
                accountRepo.updateFilters(id, request)
            }.onSuccess {
                _finishPageFlow.emit(Unit)
            }.onFailure {
                _snackBarFlow.emitTextMessageFromThrowable(it)
            }
        }
    }

    private fun EditFilterUiState.toRequest(): ActivityPubCreateFilterEntity {
        val expiresIn = if (expiresDate == null) {
            null
        } else {
            (expiresDate.toEpochMilliseconds() - getCurrentTimeMillis()) / 1000
        }
        return ActivityPubCreateFilterEntity(
            title = this.title,
            context = this.contextList.map { it.contextName },
            filterAction = if (this.filterByWarn) {
                ActivityPubFilterEntity.FILTER_ACTION_WARN
            } else {
                ActivityPubFilterEntity.FILTER_ACTION_KEYWORDS
            },
            expiresIn = expiresIn?.toInt()?.coerceAtLeast(0),
            keywordsAttributes = this.keywordList.map { keyword ->
                ActivityPubCreateFilterEntity.KeywordAttribute(
                    id = keyword.id,
                    keyword = keyword.keyword.trim(),
                    wholeWord = keyword.wholeWord,
                    destroy = keyword.deleted,
                )
            },
        )
    }

    private fun loadFilter() {
        if (id != null) {
            launchInViewModel {
                clientManager.getClient(locator)
                    .accountRepo
                    .getFilter(id)
                    .onFailure {
                        _snackBarFlow.emitTextMessageFromThrowable(it)
                    }.onSuccess {
                        _uiState.value = it.toFilter()
                    }
            }
        }
    }

    private fun ActivityPubFilterEntity.toFilter(): EditFilterUiState {
        return EditFilterUiState(
            title = this.title,
            expiresDate = expiresAt?.let(DateParser::parseAll),
            keywordList = this.keywords?.map { it.toKeyword() } ?: emptyList(),
            contextList = this.context.mapNotNull { FilterContext.fromContext(it) },
            filterByWarn = this.filterAction == ActivityPubFilterEntity.FILTER_ACTION_WARN,
            hasInputtedSomething = false,
        )
    }

    private fun ActivityPubFilterKeywordEntity.toKeyword(): EditFilterUiState.Keyword {
        return EditFilterUiState.Keyword(
            id = this.id,
            keyword = this.keyword,
            wholeWord = this.wholeWord,
        )
    }
}