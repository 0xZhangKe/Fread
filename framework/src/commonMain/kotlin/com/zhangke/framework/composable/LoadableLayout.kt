package com.zhangke.framework.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.zhangke.fread.framework.Res
import com.zhangke.fread.framework.empty
import com.zhangke.fread.framework.retry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

sealed class LoadableState<T> {

    val isSuccess: Boolean get() = this is Success

    val isFailed: Boolean get() = this is Failed

    val isIdle: Boolean get() = this is Idle

    val isLoading: Boolean get() = this is Loading

    class Idle<T> : LoadableState<T>()

    class Failed<T>(val exception: Throwable) : LoadableState<T>()

    class Loading<T> : LoadableState<T>()

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
    errorMessage: String,
    onRetryClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            fontSize = 18.sp,
            text = errorMessage,
        )
        if (onRetryClick != null) {
            Button(
                onClick = onRetryClick,
            ) {
                Text(org.jetbrains.compose.resources.stringResource(Res.string.retry))
            }
        }
    }
}

@Composable
fun BoxScope.DefaultFailed(
    modifier: Modifier = Modifier,
    exception: Throwable,
    onRetryClick: (() -> Unit)? = null,
) {
    DefaultFailed(
        modifier = modifier,
        errorMessage = exception.message.orEmpty(),
        onRetryClick = onRetryClick,
    )
}

@Composable
fun BoxScope.DefaultEmpty(
    modifier: Modifier = Modifier,
    message: String = org.jetbrains.compose.resources.stringResource(Res.string.empty),
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message)
    }
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
