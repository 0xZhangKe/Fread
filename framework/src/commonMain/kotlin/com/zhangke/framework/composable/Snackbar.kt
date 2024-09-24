package com.zhangke.framework.composable

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow

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
    messageText: TextString?,
    actionLabel: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    if (!messageText.isNullOrEmpty()) {
        val message = textString(text = messageText!!)
        LaunchedEffect(message) {
            hostState.showSnackbar(message, actionLabel, duration = duration)
        }
    }
}

@Composable
fun ConsumeSnackbarFlow(
    hostState: SnackbarHostState?,
    messageTextFlow: Flow<TextString>,
    actionLabel: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    hostState ?: return
    ConsumeFlow(messageTextFlow) {
        val message = it.getString().take(180)
        if (message.isNotEmpty()) {
            hostState.showSnackbar(message, actionLabel, duration = duration)
        }
    }
}
