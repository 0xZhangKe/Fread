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

    private val _uiState =
        MutableStateFlow(MultiAccountPublishingUiState.default(freezeLoading = true))
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
                    allAccounts = allAccounts,
                    addedAccounts = allAccounts.filter { account ->
                        defaultAddAccountList.contains(account.uri.toString())
                    },
                )
            }
        }
    }

    private suspend fun loadRules(account: LoggedAccount): Result<PublishBlogRules> {

    }
}
