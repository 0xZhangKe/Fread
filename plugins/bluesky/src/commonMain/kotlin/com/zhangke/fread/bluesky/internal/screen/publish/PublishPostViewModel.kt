package com.zhangke.fread.bluesky.internal.screen.publish

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.ReplySetting
import com.zhangke.fread.bluesky.internal.usecase.GetAllListsUseCase
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.Did

class PublishPostViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val getAllLists: GetAllListsUseCase,
    private val role: IdentityRole,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishPostUiState.default())
    val uiState = _uiState.asStateFlow()

    init {

        loadUserList()
    }

    fun onQuoteChange(allowQuote: Boolean) {
        _uiState.update {
            it.copy(interactionSetting = it.interactionSetting.copy(allowQuote = allowQuote))
        }
    }

    fun onReplySettingChange(replySetting: ReplySetting) {
        _uiState.update {
            it.copy(
                interactionSetting = it.interactionSetting.copy(replySetting = replySetting)
            )
        }
    }

    fun onSettingOptionsSelected(option: ReplySetting.CombineOption) {
        _uiState.update { state ->
            val options = state.interactionSetting.replySetting.let { it as? ReplySetting.Combined }
                ?.options?.toMutableList() ?: mutableListOf()
            if (option in options) {
                options.remove(option)
            } else {
                options.add(option)
            }
            state.copy(
                interactionSetting = state.interactionSetting.copy(
                    replySetting = ReplySetting.Combined(options),
                )
            )
        }
    }

    private fun loadUserList() {
        launchInViewModel {
            val client = clientManager.getClient(role)
            val account = client.loggedAccountProvider() ?: return@launchInViewModel
            getAllLists(role, Did(account.did))
                .onSuccess { lists -> _uiState.update { it.copy(list = lists) } }
        }
    }
}
