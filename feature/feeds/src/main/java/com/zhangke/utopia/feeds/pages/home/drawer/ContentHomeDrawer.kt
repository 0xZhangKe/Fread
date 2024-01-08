package com.zhangke.utopia.feeds.pages.home.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.status.model.ContentConfig

@Composable
fun Screen.ContentHomeDrawer() {
    val viewModel: ContentHomeViewModel = getViewModel()
    val loadableState by viewModel.uiState.collectAsState()
    ContentHomeDrawerContent(
        loadableState = loadableState,
        onContentConfigClick = {},
    )
}

@Composable
private fun ContentHomeDrawerContent(
    loadableState: LoadableState<ContentHomeDrawerUiState>,
    onContentConfigClick: (ContentConfig) -> Unit,
) {
    LoadableLayout(
        modifier = Modifier.fillMaxSize(),
        state = loadableState,
    ) { uiState ->
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        )
        {
            items(uiState.contentConfigList) { contentConfig ->
                ContentConfigItem(
                    modifier = Modifier.fillMaxWidth(),
                    contentConfig = contentConfig,
                    onClick = { onContentConfigClick(contentConfig) },
                )
            }
        }
    }
}

@Composable
private fun ContentConfigItem(
    modifier: Modifier,
    contentConfig: ContentConfig,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 16.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = contentConfig.configName,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
