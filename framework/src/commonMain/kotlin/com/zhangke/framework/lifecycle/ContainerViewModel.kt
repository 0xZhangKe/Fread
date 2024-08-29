package com.zhangke.framework.lifecycle

import androidx.lifecycle.ViewModel

abstract class ContainerViewModel<T : SubViewModel, P: ContainerViewModel.SubViewModelParams> : ViewModel() {

    abstract fun createSubViewModel(params: P): T

    private val subViewModelStore = mutableMapOf<String, T>()

    protected fun obtainSubViewModel(params: P): T {
        return subViewModelStore.getOrPut(params.key) {
            createSubViewModel(params)
        }.also { addCloseable(it) }
    }

    abstract class SubViewModelParams {

        abstract val key: String
    }
}
