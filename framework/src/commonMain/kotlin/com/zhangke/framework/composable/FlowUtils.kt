package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

@Composable
fun <T> ConsumeFlow(
    flow: Flow<T>,
    block: suspend (T) -> Unit
) {
    val updatedBlock by rememberUpdatedState(block)
    LaunchedEffect(flow) {
        flow.collect {
            updatedBlock(it)
        }
    }
}

context(viewModel: ViewModel)
fun <T> MutableSharedFlow<T>.emitInViewModel(element: T) {
    viewModel.launchInViewModel {
        emit(element)
    }
}

context(viewModel: SubViewModel)
fun <T> MutableSharedFlow<T>.emitInViewModel(element: T) {
    viewModel.launchInViewModel {
        emit(element)
    }
}
