package com.zhangke.framework.loadable.lazycolumn

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.ScrollDirection
import com.zhangke.framework.composable.rememberDirectionalLazyListState
import com.zhangke.framework.composable.textString
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.framework.R

@Composable
fun LoadMoreUi(
    loadState: LoadState,
    onLoadMore: () -> Unit,
) {
    when (loadState) {
        is LoadState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.Center)
                )
            }
        }

        is LoadState.Failed -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                var errorMessage = loadState.message?.let { textString(it) }
                if (errorMessage.isNullOrEmpty()) {
                    errorMessage = stringResource(R.string.load_more_error)
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = errorMessage,
                    textAlign = TextAlign.Center,
                )
                TextButton(
                    modifier = Modifier.padding(top = 6.dp),
                    onClick = onLoadMore,
                ) {
                    Text(text = stringResource(R.string.retry))
                }
            }
        }

        else -> {}
    }
}

@Composable
fun ObserveLazyListLoadEvent(
    lazyListState: LazyListState,
    loadPreviousPageRemainCountThreshold: Int,
    loadMoreRemainCountThreshold: Int,
    onLoadPrevious: () -> Unit,
    onLoadMore: () -> Unit,
) {
    val listLayoutInfo by remember { derivedStateOf { lazyListState.layoutInfo } }
    val directional = rememberDirectionalLazyListState(lazyListState).scrollDirection
    val totalItemsCount = listLayoutInfo.totalItemsCount
    var inLoadPreviousZone by remember {
        mutableStateOf(false)
    }
    val currentFirstVisibleIndex = listLayoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
    inLoadPreviousZone = totalItemsCount > 0 &&
            currentFirstVisibleIndex <= loadPreviousPageRemainCountThreshold &&
            totalItemsCount > loadPreviousPageRemainCountThreshold
    LaunchedEffect(inLoadPreviousZone, directional) {
        if (inLoadPreviousZone && directional == ScrollDirection.Up) {
            onLoadPrevious()
        }
    }
    var inLoadingMoreZone by remember {
        mutableStateOf(false)
    }
    val currentLastVisibleIndex = listLayoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    val remainToBottomCount = totalItemsCount - currentLastVisibleIndex - 1
    inLoadingMoreZone = totalItemsCount > 0 &&
            remainToBottomCount <= loadMoreRemainCountThreshold &&
            totalItemsCount > loadMoreRemainCountThreshold
    LaunchedEffect(inLoadingMoreZone, directional) {
        if (inLoadingMoreZone) {
            onLoadMore()
        }
    }
}
