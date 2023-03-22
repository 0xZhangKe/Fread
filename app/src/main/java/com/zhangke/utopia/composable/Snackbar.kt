package com.zhangke.utopia.composable

import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

@Composable
fun rememberSnackbarHostState(): SnackbarHostState {
    return remember { SnackbarHostState() }
}

@Composable
fun snackbarHost(hostState: SnackbarHostState): @Composable (() -> Unit) {
    return {
        SnackbarHost(hostState = hostState)
    }
}

@Composable
fun ObserveSnackbar(
    hostState: SnackbarHostState,
    messageText: Text?,
    actionLabel: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    if (!messageText.isNullOrEmpty()) {
        val message = textString(text = messageText!!)
        LaunchedEffect(message) {
            hostState.showSnackbar(message, actionLabel, duration)
        }
    }
}