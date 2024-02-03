package com.zhangke.utopia.explore.screens.search

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import javax.inject.Inject

@HiltViewModel
class SearchContentViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
): ViewModel() {

    private var searchJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        searchJob = launchInViewModel {
            statusProvider.searchEngine
                .searchSource(query)
        }
    }
}
