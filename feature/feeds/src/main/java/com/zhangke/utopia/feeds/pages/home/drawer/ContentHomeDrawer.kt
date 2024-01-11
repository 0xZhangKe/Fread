package com.zhangke.utopia.feeds.pages.home.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.utopia.status.model.ContentConfig

@Composable
fun ContentHomeDrawer(
    contentConfigList: List<ContentConfig>,
    onContentConfigClick: (ContentConfig) -> Unit,
    onAddContentClick: () -> Unit,
) {
    ContentHomeDrawerContent(
        contentConfigList = contentConfigList,
        onContentConfigClick = onContentConfigClick,
        onAddContentClick = onAddContentClick,
    )
}

@Composable
private fun ContentHomeDrawerContent(
    contentConfigList: List<ContentConfig>,
    onContentConfigClick: (ContentConfig) -> Unit,
    onAddContentClick: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
            ) {
                items(contentConfigList) { contentConfig ->
                    ContentConfigItem(
                        modifier = Modifier.fillMaxWidth(),
                        contentConfig = contentConfig,
                        onClick = { onContentConfigClick(contentConfig) },
                    )
                }
            }
            Button(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = onAddContentClick
            ) {
                Text(text = "Add")
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
    Box(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterStart),
            text = contentConfig.configName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
