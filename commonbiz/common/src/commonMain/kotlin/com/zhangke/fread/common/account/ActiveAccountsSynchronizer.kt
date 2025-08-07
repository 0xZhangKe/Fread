package com.zhangke.fread.common.account

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@ApplicationScope
class ActiveAccountsSynchronizer @Inject constructor(
    private val freadConfigManager: FreadConfigManager,
) {

    private val _activeAccountUriFlow = MutableStateFlow<String?>(null)
    val activeAccountUriFlow: StateFlow<String?> = _activeAccountUriFlow

    fun initialize() {
        ApplicationScope.launch {
            freadConfigManager.getLastSelectedAccount()?.let {
                _activeAccountUriFlow.value = it
            }
        }
    }

    suspend fun onAccountSelected(accountUri: String) {
        _activeAccountUriFlow.value = accountUri
        freadConfigManager.updateLastSelectedAccount(accountUri)
    }
}
