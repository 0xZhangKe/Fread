package com.zhangke.framework.loadable.previous

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.TextString
import com.zhangke.utopia.framework.R
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface PreviousPageLoadingState {

    data object Idle : PreviousPageLoadingState

    data object Loading : PreviousPageLoadingState

    data class Failed(val errorMessage: TextString?) : PreviousPageLoadingState
}

data class LoadPreviousPageUiState(
    val onLoadPreviousPage: () -> Unit,
    val initialState: PreviousPageLoadingState,
    val loadPreviousPageThreshold: Int,
) {

    internal val loadingState = MutableStateFlow(initialState)

    fun update(state: PreviousPageLoadingState) {
        loadingState.value = state
    }
}

@Composable
fun rememberLoadPreviousPageUiState(
    onLoadPreviousPage: () -> Unit,
    initialState: PreviousPageLoadingState = PreviousPageLoadingState.Idle,
    loadPreviousPageThreshold: Int = 3,
): LoadPreviousPageUiState {
    return remember(onLoadPreviousPage, loadPreviousPageThreshold) {
        LoadPreviousPageUiState(
            onLoadPreviousPage = onLoadPreviousPage,
            initialState = initialState,
            loadPreviousPageThreshold = loadPreviousPageThreshold,
        )
    }
}

@Composable
fun LoadPreviousPageItem(
    modifier: Modifier,
    state: PreviousPageLoadingState,
    onLoadPreviousPage: () -> Unit,
) {
    when (state) {
        is PreviousPageLoadingState.Loading -> {
            LoadingPreviousUi(modifier)
        }

        is PreviousPageLoadingState.Failed -> {
            LoadPreviousFailedUi(modifier, onLoadPreviousPage)
        }

        else -> {}
    }
}

@Composable
private fun LoadingPreviousUi(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.feeds_load_previous_page_label),
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.width(6.dp))
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun LoadPreviousFailedUi(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
        ) {
            Text(
                modifier = Modifier
                    .padding(vertical = 6.dp)
                    .align(Alignment.CenterHorizontally),
                text = stringResource(R.string.feeds_load_previous_page_failed_label),
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}
