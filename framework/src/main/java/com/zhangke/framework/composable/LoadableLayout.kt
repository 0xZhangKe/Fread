package com.zhangke.framework.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed class LoadableState<T> {

    val isSuccess: Boolean get() = this is Success

    val isFailed: Boolean get() = this is Failed

    val isIdle: Boolean get() = this is Idle

    val isLoading: Boolean get() = this is Loading

    class Idle<T> : LoadableState<T>()

    class Failed<T>(val exception: Throwable) : LoadableState<T>()

    class Loading<T>() : LoadableState<T>()

    class Success<T>(val data: T) : LoadableState<T>()

    companion object {

        fun <T> idle(): LoadableState<T> {
            return Idle()
        }

        fun <T> success(data: T): LoadableState<T> {
            return Success(data)
        }

        fun <T> loading(): LoadableState<T> {
            return Loading()
        }

        fun <T> failed(exception: Throwable): LoadableState<T> {
            return Failed(exception)
        }
    }
}

@Composable
fun <T> LoadableLayout(
    modifier: Modifier = Modifier,
    state: LoadableState<T>,
    failed: (@Composable BoxScope.(Throwable) -> Unit)? = null,
    loading: (@Composable BoxScope.() -> Unit)? = null,
    idle: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.(T) -> Unit,
) {
    Box(modifier = modifier) {
        when (state) {
            is LoadableState.Loading -> {
                loading?.invoke(this) ?: DefaultLoading()
            }

            is LoadableState.Failed -> {
                failed?.invoke(this, state.exception) ?: DefaultFailed(exception = state.exception)
            }

            is LoadableState.Success -> {
                content(state.data)
            }

            is LoadableState.Idle -> {
                idle?.invoke(this) ?: DefaultIdle()
            }
        }
    }
}

@Composable
fun BoxScope.DefaultLoading(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier
            .align(Alignment.Center)
            .fillMaxWidth(0.3F)
    )
}

@Composable
fun BoxScope.DefaultFailed(
    modifier: Modifier = Modifier,
    exception: Throwable,
) {
    Text(
        modifier = modifier
            .align(Alignment.Center),
        fontSize = 18.sp,
        text = exception.message.orEmpty(),
    )
}

@Composable
fun BoxScope.DefaultIdle(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier)
}

fun <T> MutableStateFlow<LoadableState<T>>.updateToSuccess(
    data: T
) {
    update {
        LoadableState.success(data)
    }
}

fun <T> MutableStateFlow<LoadableState<T>>.updateToLoading() {
    update {
        LoadableState.loading()
    }
}

fun <T> MutableStateFlow<LoadableState<T>>.updateToFailed(e: Throwable) {
    update {
        LoadableState.failed(e)
    }
}

fun <T> MutableStateFlow<LoadableState<T>>.updateOnSuccess(
    updater: (T) -> T,
) {
    update {
        if (it.isSuccess) {
            val data = it.requireSuccessData()
            LoadableState.success(updater(data))
        } else {
            it
        }
    }
}

fun <T> LoadableState<T>.requireSuccessData(): T {
    return (this as LoadableState.Success).data
}

fun <T> LoadableState<T>.successDataOrNull(): T? {
    return (this as? LoadableState.Success)?.data
}
