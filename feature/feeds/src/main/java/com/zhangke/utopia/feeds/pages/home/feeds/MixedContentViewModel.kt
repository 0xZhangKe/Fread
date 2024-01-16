package com.zhangke.utopia.feeds.pages.home.feeds

import androidx.lifecycle.ViewModel
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MixedContentViewModel @Inject constructor(
    private val contentConfigRepo: ContentConfigRepo,
    private val feedsRepo: FeedsRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val subViewModelStore = mutableMapOf<String, MixedContentSubViewModel>()

    fun getSubViewModel(configId: Long): MixedContentSubViewModel {
        val key = configId.toString()
        subViewModelStore[key]?.let { return it }
        val subViewModel = MixedContentSubViewModel(
            contentConfigRepo = contentConfigRepo,
            feedsRepo = feedsRepo,
            buildStatusUiState = buildStatusUiState,
            statusProvider = statusProvider,
            configId = configId,
        )
        addCloseable(subViewModel)
        return subViewModel
    }
}
