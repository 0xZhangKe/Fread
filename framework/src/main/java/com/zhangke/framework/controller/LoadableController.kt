package com.zhangke.framework.controller

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.utils.LoadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * type DATA is the type of the data to be loaded,
 * type IMPL is the type of the implementation of the LoadableUiState.
 */
interface LoadableUiState<DATA, IMPL : LoadableUiState<DATA, IMPL>> {

    val dataList: List<DATA>

    val refreshing: Boolean

    val loadMoreState: LoadState

    val errorMessage: TextString?

    fun copyObject(
        dataList: List<DATA> = this.dataList,
        refreshing: Boolean = this.refreshing,
        loadMoreState: LoadState = this.loadMoreState,
        errorMessage: TextString? = this.errorMessage,
    ): IMPL
}

/**
 * type DATA is the type of the data to be loaded,
 * type IMPL is the type of the implementation of the LoadableUiState.
 */
open class LoadableController<DATA, IMPL : LoadableUiState<DATA, IMPL>>(
    private val coroutineScope: CoroutineScope,
    initialUiState: IMPL,
) {

    val mutableUiState: MutableStateFlow<IMPL> = MutableStateFlow(initialUiState)
    val uiState = mutableUiState.asStateFlow()

    private var initJob: Job? = null
    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    /**
     * 一般来说初始化的时候调用一次，之后只需要调用 onRefresh 和 onLoadMore 即可。
     * 如果提供了 getDataFromLocal 参数，那么会先从本地获取数据，然后再调用 getDataFromServer。
     */
    fun initData(
        getDataFromServer: suspend () -> Result<List<DATA>>,
        getDataFromLocal: (suspend () -> List<DATA>)? = null,
    ) {
        mutableUiState.update {
            it.copyObject(dataList = emptyList())
        }
        initJob?.cancel()
        initJob = coroutineScope.launch {
            if (getDataFromLocal != null) {
                val localData = getDataFromLocal()
                if (localData.isNotEmpty()) {
                    mutableUiState.update {
                        it.copyObject(dataList = localData)
                    }
                }
            }
            getDataFromServer().handleAsRefresh()
        }
    }

    fun onRefresh(
        hideRefreshing: Boolean = false,
        getDataFromServer: suspend () -> Result<List<DATA>>,
    ) {
        if (mutableUiState.value.refreshing) return
        mutableUiState.update {
            it.copyObject(refreshing = !hideRefreshing, errorMessage = null)
        }
        loadMoreJob?.cancel()
        refreshJob?.cancel()
        refreshJob = coroutineScope.launch {
            getDataFromServer().handleAsRefresh()
        }
    }

    private fun Result<List<DATA>>.handleAsRefresh() {
        this.onSuccess { list ->
            mutableUiState.update {
                it.copyObject(
                    dataList = list,
                    refreshing = false,
                )
            }
        }.onFailure { e ->
            val errorMessage = e.message?.let { textOf(it) }
            mutableUiState.update {
                it.copyObject(
                    errorMessage = errorMessage,
                    refreshing = false,
                )
            }
        }
    }

    fun onLoadMore(
        loadMoreFromServer: suspend () -> Result<List<DATA>>,
    ) {
        if (mutableUiState.value.refreshing) return
        if (mutableUiState.value.loadMoreState == LoadState.Loading) return
        mutableUiState.update { it.copyObject(loadMoreState = LoadState.Loading) }
        loadMoreJob?.cancel()
        loadMoreJob = coroutineScope.launch {
            loadMoreFromServer()
                .onSuccess { list ->
                    mutableUiState.update {
                        it.copyObject(
                            dataList = it.dataList + list,
                            loadMoreState = LoadState.Idle,
                        )
                    }
                }.onFailure { e ->
                    mutableUiState.update {
                        it.copyObject(loadMoreState = LoadState.Failed(e.toTextStringOrNull()))
                    }
                }
        }
    }
}
