package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusTableEntity
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ActivityPubTimelineViewModel(
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    refactorToNewBlog: RefactorToNewBlogUseCase,
    private val timelineRepo: ActivityPubTimelineStatusRepo,
    private val role: IdentityRole,
    private val type: ActivityPubStatusSourceType,
    private val listId: String?,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    private val _uiState = MutableStateFlow(ActivityPubTimelineUiState.default)
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessageFlow.asSharedFlow()

    private var initFeedsJob: Job? = null

    init {

    }

    private fun initFeeds() {
        initFeedsJob?.cancel()
        initFeedsJob = launchInViewModel {
            _uiState.update { it.copy(showPagingLoadingPlaceholder = true) }
            val localStatus = timelineRepo.getStatusFromLocal(
                role = role,
                type = type,
                listId = listId,
            )
            if (localStatus.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        items = localStatus.toTimelineItems(),
                        showPagingLoadingPlaceholder = false,
                    )
                }
            }
            val sinceId = localStatus.firstOrNull()?.status?.id
            timelineRepo.getStatusFromServer(
                role = role,
                type = type,
                sinceId = sinceId,
                listId = listId,
            ).map {
                it.toTimelineItems()
            }.onFailure { t ->
                if (_uiState.value.items.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            showPagingLoadingPlaceholder = false,
                            pageErrorContent = t.toTextStringOrNull(),
                        )
                    }
                } else {
                    _snackBarMessageFlow.emitTextMessageFromThrowable(t)
                }
            }.onSuccess { list ->
                _uiState.update {
                    val newList = list.toMutableList()
                    newList.addAll(it.items)
                    it.copy(
                        items = newList,
                        showPagingLoadingPlaceholder = false,
                    )
                }
            }
        }
    }

    private fun List<ActivityPubStatusTableEntity>.toTimelineItems(): List<ActivityPubTimelineItem> {
        val timelines = mutableListOf<ActivityPubTimelineItem>()
        this.forEach {
            timelines += it.toActivityPubTimelineItem()
            if (it.fracture) {
                timelines += ActivityPubTimelineItem.FractureItem
            }
        }
        return timelines
    }

    private fun ActivityPubStatusTableEntity.toActivityPubTimelineItem(): ActivityPubTimelineItem {
        return ActivityPubTimelineItem.StatusItem(buildStatusUiState(role, this.status))
    }

    private fun List<Status>.toTimelineItems(): List<ActivityPubTimelineItem> {
        return map { ActivityPubTimelineItem.StatusItem(buildStatusUiState(role, it)) }
    }

    private fun Status.toTimelineItem(): ActivityPubTimelineItem {
        return ActivityPubTimelineItem.StatusItem(buildStatusUiState(role, this))
    }
}
