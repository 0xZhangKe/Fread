package com.zhangke.framework.loadable.lazycolumn

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.LocalContentPadding

@Composable
fun BoxScope.PullToRefreshIndicator(
    state: PullToRefreshState,
    refreshing: Boolean,
) {
    PullToRefreshDefaults.Indicator(
        state = state,
        isRefreshing = refreshing,
        modifier = Modifier.align(Alignment.TopCenter)
            .padding(LocalContentPadding.current.calculateTopPadding()),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}
