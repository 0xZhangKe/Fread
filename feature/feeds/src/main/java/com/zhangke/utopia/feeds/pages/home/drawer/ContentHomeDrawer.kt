package com.zhangke.utopia.feeds.pages.home.drawer

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.utopia.feeds.pages.home.EmptyContent
import com.zhangke.utopia.status.model.ContentConfig
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun ContentHomeDrawer(
    contentConfigList: List<ContentConfig>,
    onContentConfigClick: (ContentConfig) -> Unit,
    onAddContentClick: () -> Unit,
    onImportClick: () -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
    onEditClick: (ContentConfig) -> Unit,
) {
    ContentHomeDrawerContent(
        contentConfigList = contentConfigList,
        onContentConfigClick = onContentConfigClick,
        onAddContentClick = onAddContentClick,
        onMove = onMove,
        onEditClick = onEditClick,
        onImportClick = onImportClick,
    )
}

@Composable
private fun ContentHomeDrawerContent(
    contentConfigList: List<ContentConfig>,
    onContentConfigClick: (ContentConfig) -> Unit,
    onImportClick: () -> Unit,
    onAddContentClick: () -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
    onEditClick: (ContentConfig) -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        if (contentConfigList.isEmpty()) {
            EmptyContent(Modifier.fillMaxSize(), onAddContentClick)
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                var configListInUi by remember(contentConfigList) {
                    mutableStateOf(contentConfigList)
                }
                key(contentConfigList) {
                    val state = rememberReorderableLazyListState(
                        onMove = { from, to ->
                            if (contentConfigList.isEmpty()) return@rememberReorderableLazyListState
                            configListInUi = configListInUi.toMutableList().apply {
                                add(to.index, removeAt(from.index))
                            }
                        },
                        onDragEnd = { startIndex, endIndex ->
                            onMove(startIndex, endIndex)
                        }
                    )
                    LazyColumn(
                        state = state.listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1F)
                            .reorderable(state)
                            .detectReorderAfterLongPress(state)
                    ) {
                        itemsIndexed(
                            items = configListInUi,
                            key = { _, item -> item.hashCode() }
                        ) { _, contentConfig ->
                            ReorderableItem(state, contentConfig.hashCode()) { dragging ->
                                val elevation =
                                    animateDpAsState(if (dragging) 16.dp else 0.dp, label = "")
                                ContentConfigItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(elevation.value),
                                    contentConfig = contentConfig,
                                    onClick = { onContentConfigClick(contentConfig) },
                                    onEditClick = onEditClick,
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = onImportClick,
                    ) {
                        Text(text = "import")
                    }

                    Button(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = onAddContentClick,
                    ) {
                        Text(text = "Add")
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentConfigItem(
    modifier: Modifier,
    contentConfig: ContentConfig,
    onClick: () -> Unit,
    onEditClick: (ContentConfig) -> Unit,
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = contentConfig.configName,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.weight(1F))
        SimpleIconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { onEditClick(contentConfig) },
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Content Config",
        )
    }
}
