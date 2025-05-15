package com.zhangke.fread.commonbiz.shared.screen.publish.multi

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PublishBlogRules
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class MultiAccountPublishingViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    @Assisted private val defaultAddAccountList: List<String>,
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
            val addedAccounts = allAccounts.filter { account ->
                defaultAddAccountList.contains(account.uri.toString())
            }
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
        }
    }

    fun onAddAccount(account: MultiPublishingAccountWithRules) {
        _uiState.update {
            it.copy(addedAccounts = it.addedAccounts + account.toDefaultUiState())
        }
        if (account.rules == null) {
            loadRuleForAccount(account.account)
        }
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
