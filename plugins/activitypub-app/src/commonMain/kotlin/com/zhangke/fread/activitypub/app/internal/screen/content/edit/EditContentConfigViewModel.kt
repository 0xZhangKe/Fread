package com.zhangke.fread.activitypub.app.internal.screen.content.edit

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.usecase.content.ReorderActivityPubTabUseCase
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.localization.LocalizedString
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
class EditContentConfigViewModel (
    private val contentRepo: FreadContentRepo,
    private val reorderTab: ReorderActivityPubTabUseCase,
    private val contentId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<EditContentConfigUiState?>(null)
    val uiState = _uiState.asStateFlow()
    private val _snackbarMessageFlow = MutableSharedFlow<TextString>()
    val snackbarMessageFlow = _snackbarMessageFlow.asSharedFlow()
    private val _finishScreenFlow = MutableSharedFlow<Unit>()
    val finishScreenFlow = _finishScreenFlow.asSharedFlow()

    init {
        launchInViewModel {
            contentRepo.getContentFlow(contentId)
                .collect { content ->
                    if (content !is ActivityPubContent) {
                        _snackbarMessageFlow.emit(textOf(LocalizedString.activity_pub_edit_content_screen_config_not_found))
                        return@collect
                    }
                    _uiState.value = EditContentConfigUiState(content)
                }
        }
    }

    fun onShowingTabMove(from: Int, to: Int) {
        val uiState = _uiState.value ?: return
        val content = uiState.content
        launchInViewModel {
            reorderTab(
                content = content,
                fromTab = content.tabList[from],
                toTab = content.tabList[to],
            )
        }
    }

    fun onShowingTabMoveDown(tab: ActivityPubContent.ContentTab) {
        launchInViewModel {
            updateTabHideState(tab, true)
        }
    }

    fun onHiddenTabMoveUp(tab: ActivityPubContent.ContentTab) {
        launchInViewModel {
            updateTabHideState(tab, false)
        }
    }

    private suspend fun updateTabHideState(
        tab: ActivityPubContent.ContentTab,
        hide: Boolean,
    ) {
        val content = _uiState.value?.content ?: return
        val newContent = content.copy(
            tabList = content.tabList.map {
                if (it == tab) {
                    it.updateHide(hide)
                } else {
                    it
                }
            }
        )
        contentRepo.insertContent(newContent)
    }

    fun onDeleteClick() {
        launchInViewModel {
            contentRepo.delete(contentId)
            _finishScreenFlow.emit(Unit)
        }
    }

    fun onEditNameClick(contentName: String) {
        launchInViewModel {
            if (contentRepo.checkNameExist(contentName)) {
                _snackbarMessageFlow.emit(textOf(LocalizedString.addFeedsPageEmptyNameExist))
                return@launchInViewModel
            }
            val newContent = contentRepo.getContent(contentId)
                ?.let { it as? ActivityPubContent }
                ?.copy(name = contentName) ?: return@launchInViewModel
            contentRepo.insertContent(newContent)
        }
    }
}