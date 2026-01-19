package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitInViewModel
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.ifNullOrEmpty
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.initLocale
import com.zhangke.framework.utils.languageCode
import com.zhangke.fread.common.utils.PlatformUriHelper
import com.zhangke.fread.commonbiz.shared.repo.SelectedAccountPublishingRepo
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishPostMedia
import com.zhangke.fread.commonbiz.shared.usecase.PublishPostOnMultiAccountUseCase
import com.zhangke.fread.commonbiz.shared.usecase.PublishingPartFailed
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PostInteractionSetting
import com.zhangke.fread.status.model.PublishBlogRules
import com.zhangke.fread.status.model.StatusVisibility
import com.zhangke.fread.status.publish.PublishingMedia
import com.zhangke.fread.status.publish.PublishingPost
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MultiAccountPublishingViewModel (
    private val statusProvider: StatusProvider,
    private val platformUriHelper: PlatformUriHelper,
    private val publishPostOnMultiAccount: PublishPostOnMultiAccountUseCase,
    private val selectedAccountPublishingRepo: SelectedAccountPublishingRepo,
    private val defaultAddAccountList: List<String>,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MultiAccountPublishingUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackMessage = MutableSharedFlow<TextString>()
    val snackMessage: SharedFlow<TextString> get() = _snackMessage

    private val _publishSuccessFlow = MutableSharedFlow<Unit>()
    val publishSuccessFlow: SharedFlow<Unit> get() = _publishSuccessFlow

    init {
        launchInViewModel {
            val allAccounts = statusProvider.accountManager.getAllLoggedAccount()
            val addedAccounts = getInitialAccount(allAccounts)
            _uiState.update {
                it.copy(
                    addedAccounts = addedAccounts.map { it.toDefaultUiState() },
                    allAccounts = allAccounts.map { MultiPublishingAccountWithRules(it, null) },
                )
            }
            val addedAccountUiState = addedAccounts.map { account ->
                val rules = loadRules(account) ?: MultiAccountPublishingUiState.defaultRules()
                MultiPublishingAccountUiState(account, rules)
            }
            _uiState.update { state ->
                state.copy(
                    addedAccounts = addedAccountUiState,
                    allAccounts = state.allAccounts.map { (account, _) ->
                        val rules =
                            addedAccountUiState.firstOrNull { it.account.uri == account.uri }?.rules
                        MultiPublishingAccountWithRules(account, rules)
                    },
                )
            }
            updateGlobalRules()
        }
    }

    private suspend fun getInitialAccount(allLoggedAccounts: List<LoggedAccount>): List<LoggedAccount> {
        val pendingAddAccounts = selectedAccountPublishingRepo.getAll() + defaultAddAccountList
        return allLoggedAccounts.filter { account ->
            pendingAddAccounts.contains(account.uri.toString())
        }
    }

    fun onAddAccount(account: MultiPublishingAccountWithRules) {
        if (uiState.value.addedAccounts.any { it.account.uri == account.account.uri }) return
        _uiState.update {
            it.copy(addedAccounts = it.addedAccounts + account.toDefaultUiState())
        }
        if (account.rules == null) {
            loadRuleForAccount(account.account)
        }
        updateGlobalRules()
        updateLocalAccounts()
    }

    fun onRemoveAccountClick(account: LoggedAccount) {
        if (uiState.value.addedAccounts.size <= 1) return
        _uiState.update {
            it.copy(addedAccounts = it.addedAccounts.filter { it.account.uri != account.uri })
        }
        updateGlobalRules()
        updateLocalAccounts()
    }

    private fun updateLocalAccounts() {
        launchInViewModel {
            val addedAccounts = _uiState.value.addedAccounts
            selectedAccountPublishingRepo.replace(addedAccounts.map { it.account.uri.toString() })
        }
    }

    fun onContentChanged(content: TextFieldValue) {
        _uiState.update {
            it.copy(content = content)
        }
    }

    fun onSensitiveClick() {
        _uiState.update {
            it.copy(sensitive = !it.sensitive)
        }
    }

    fun onWarningContentChanged(content: TextFieldValue) {
        _uiState.update { it.copy(warningContent = content) }
    }

    fun onMediaAltChanged(media: PublishPostMedia, alt: String) {
        _uiState.update {
            it.copy(
                medias = it.medias.map { m ->
                    if (m.uri == media.uri) m.copy(alt = alt) else m
                },
            )
        }
    }

    fun onDeleteMediaClick(media: PublishPostMedia) {
        _uiState.update {
            it.copy(medias = it.medias.filter { m -> m.uri != media.uri })
        }
    }

    fun onVisibilitySelect(visibility: StatusVisibility) {
        _uiState.update { it.copy(postVisibility = visibility) }
    }

    fun onSettingSelected(setting: PostInteractionSetting) {
        _uiState.update { it.copy(interactionSetting = setting) }
    }

    private fun loadRuleForAccount(account: LoggedAccount) {
        launchInViewModel {
            val rules = loadRules(account) ?: return@launchInViewModel
            _uiState.update { state ->
                state.copy(
                    allAccounts = state.allAccounts.map {
                        if (it.account.uri == account.uri) {
                            it.copy(rules = rules)
                        } else {
                            it
                        }
                    },
                    addedAccounts = state.addedAccounts.map {
                        if (it.account.uri == account.uri) {
                            it.copy(rules = rules)
                        } else {
                            it
                        }
                    },
                )
            }
        }
    }

    fun onPublishClick() {
        val uiState = _uiState.value
        if (uiState.medias.isEmpty() && uiState.content.text.isEmpty()) {
            _snackMessage.emitInViewModel(textOf(LocalizedString.postStatusContentIsEmpty))
            return
        }
        launchInViewModel {
            _uiState.update { it.copy(publishing = true) }
            publishPostOnMultiAccount(
                accounts = uiState.addedAccounts.map { it.account },
                publishingPost = uiState.obtainPost(),
            ).onFailure { t ->
                _uiState.update { it.copy(publishing = false) }
                if (t is PublishingPartFailed) {
                    _snackMessage.emit(
                        textOf(LocalizedString.postStatusPartFailed, t.message.orEmpty())
                    )
                    _uiState.update { state ->
                        state.copy(
                            addedAccounts = state.addedAccounts.filter {
                                !t.successAccount.contains(it.account.uri.toString())
                            },
                        )
                    }
                } else {
                    val errorMessage = textOf(
                        LocalizedString.postStatusFailed,
                        t.message.ifNullOrEmpty { "unknown error" }.take(180),
                    )
                    _snackMessage.emit(errorMessage)
                }
            }.onSuccess {
                _uiState.update { it.copy(publishing = false) }
                _publishSuccessFlow.emit(Unit)
            }
        }
    }

    private fun MultiAccountPublishingUiState.obtainPost(): PublishingPost {
        return PublishingPost(
            content = this.content.text,
            visibility = this.postVisibility,
            interactionSetting = this.interactionSetting,
            sensitive = this.sensitive,
            warningText = this.warningContent.text,
            languageCode = this.selectedLanguage.languageCode,
            medias = this.medias.map { it.convert() }
        )
    }

    private fun PublishPostMediaAttachmentFile.convert(): PublishingMedia {
        return PublishingMedia(
            file = this.file,
            alt = this.alt.orEmpty(),
            isVideo = isVideo,
        )
    }

    fun onMediaSelected(medias: List<PlatformUri>) {
        if (medias.isEmpty()) return
        launchInViewModel {
            val fileList = medias.map { async { platformUriHelper.read(it) } }
                .awaitAll().filterNotNull()
            val newMedias = if (fileList.first().isVideo) {
                PublishPostMediaAttachmentFile(
                    file = fileList.first(),
                    isVideo = true,
                    alt = null,
                ).let { listOf(it) }
            } else {
                uiState.value.medias + fileList.map {
                    PublishPostMediaAttachmentFile(
                        file = it,
                        isVideo = false,
                        alt = null,
                    )
                }
            }
            _uiState.update { it.copy(medias = newMedias) }
        }
    }

    fun onLanguageSelected(lan: String) {
        _uiState.update { it.copy(selectedLanguage = initLocale(lan)) }
    }

    private fun updateGlobalRules() {
        val addedAccount = uiState.value.addedAccounts
        val maxCharacters = addedAccount.minOf { it.rules.maxCharacters }
        val maxMediaCount = addedAccount.minOf { it.rules.maxMediaCount }
        _uiState.update {
            it.copy(
                globalRules = it.globalRules.copy(
                    maxCharacters = maxCharacters,
                    maxMediaCount = maxMediaCount,
                )
            )
        }
    }

    private fun LoggedAccount.toDefaultUiState(): MultiPublishingAccountUiState {
        return MultiPublishingAccountUiState(
            account = this,
            rules = MultiAccountPublishingUiState.defaultRules(),
        )
    }

    private fun MultiPublishingAccountWithRules.toDefaultUiState(): MultiPublishingAccountUiState {
        return MultiPublishingAccountUiState(
            account = this.account,
            rules = this.rules ?: MultiAccountPublishingUiState.defaultRules(),
        )
    }

    private suspend fun loadRules(account: LoggedAccount): PublishBlogRules? {
        return statusProvider.publishManager.getPublishBlogRules(account).getOrNull()
    }
}