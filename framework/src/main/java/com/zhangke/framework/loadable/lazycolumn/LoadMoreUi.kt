package com.zhangke.framework.loadable.lazycolumn

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.textString
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.framework.R

@Composable
fun LoadMoreUi(
    loadState: LoadState,
    onLoadMore: () -> Unit,
) {
    when (loadState) {
        is LoadState.Loading -> {
            Box(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(30.dp)
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
