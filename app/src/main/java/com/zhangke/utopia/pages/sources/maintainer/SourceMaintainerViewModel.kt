package com.zhangke.utopia.pages.sources.maintainer

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.status.domain.ResolveSourceMaintainerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SourceMaintainerViewModel @Inject constructor(
    private val resolveSourceMaintainerUseCase: ResolveSourceMaintainerUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initState())
    val uiState = _uiState.asStateFlow()

    fun prepare(query: String) {
        _uiState.update {
            it.copy(maintainerState = LoadableState.loading())
        }
        launchInViewModel {
            resolveSourceMaintainerUseCase(query)
                .onSuccess { maintainer ->
                    _uiState.update {
                        it.copy(maintainerState = LoadableState.success(maintainer))
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(maintainerState = LoadableState.failed(e))
                    }
                }
        }
    }

    private fun initState() = SourceMaintainerUiState(
        title = "",
        errorMessageText = null,
        maintainerState = LoadableState.loading(),
    )
}
