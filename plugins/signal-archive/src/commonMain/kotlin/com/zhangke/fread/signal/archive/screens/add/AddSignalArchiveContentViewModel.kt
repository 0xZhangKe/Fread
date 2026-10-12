package com.zhangke.fread.signal.archive.screens.add

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.deeplink.SelectedContentSwitcher
import com.zhangke.fread.common.onboarding.OnboardingComponent
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.signal.archive.content.SignalArchiveContent
import com.zhangke.fread.signal.archive.repo.SignalArchiveRepo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class AddSignalArchiveContentUiState(
    val url: String = "",
    val name: String = "",
    val saving: Boolean = false,
)

class AddSignalArchiveContentViewModel(
    private val archiveRepo: SignalArchiveRepo,
    private val contentRepo: FreadContentRepo,
    private val onboarding: OnboardingComponent,
    private val contentSwitcher: SelectedContentSwitcher,
    initialUrl: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AddSignalArchiveContentUiState(url = initialUrl))
    val uiState = _uiState.asStateFlow()
    private val _snackbarMessage = MutableSharedFlow<TextString>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()
    private val _successFlow = MutableSharedFlow<Unit>()
    val successFlow = _successFlow.asSharedFlow()

    fun onUrlChanged(url: String) {
        if (!_uiState.value.saving) _uiState.update { it.copy(url = url) }
    }

    fun onNameChanged(name: String) {
        if (!_uiState.value.saving) _uiState.update { it.copy(name = name) }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun onAddClick() {
        val state = _uiState.value
        if (state.saving) return
        _uiState.update { it.copy(saving = true) }
        launchInViewModel {
            try {
                val name = state.name.trim()
                val baseUrl = signalArchiveBaseUrl(state.url)
                val error = when {
                    name.isEmpty() -> LocalizedString.addFeedsPageEmptyNameTips
                    baseUrl == null -> LocalizedString.signalArchiveInvalidAddress
                    contentRepo.checkNameExist(name) -> LocalizedString.addFeedsPageEmptyNameExist
                    contentRepo.getAllContent().filterIsInstance<SignalArchiveContent>()
                        .any { it.baseUrl == baseUrl } -> LocalizedString.contentExistTips
                    else -> null
                }
                if (error != null) {
                    _snackbarMessage.emit(textOf(error))
                    return@launchInViewModel
                }
                if (archiveRepo.getIndex(baseUrl!!).isFailure) {
                    _snackbarMessage.emit(textOf(LocalizedString.signalArchiveLoadFailed))
                    return@launchInViewModel
                }
                val content = SignalArchiveContent(
                    id = Uuid.random().toHexString(),
                    order = contentRepo.getMaxOrder() + 1,
                    name = name,
                    baseUrl = baseUrl,
                )
                contentRepo.insertContent(content)
                contentSwitcher.switchToContent(content)
                _successFlow.emit(Unit)
                onboarding.onboardingSuccess()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                _snackbarMessage.emit(textOf(LocalizedString.signalArchiveAddFailed))
            } finally {
                _uiState.update { it.copy(saving = false) }
            }
        }
    }
}
