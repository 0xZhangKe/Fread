package com.zhangke.fread.screen.main.drawer

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.fread.commonbiz.shared.LocalModuleScreenVisitor
import com.zhangke.fread.feeds.pages.home.EmptyContent
import com.zhangke.fread.feeds.pages.manager.add.pre.PreAddFeedsScreen
import com.zhangke.fread.hosting.Res
import com.zhangke.fread.hosting.main_drawer_donate
import com.zhangke.fread.hosting.main_drawer_settings
import com.zhangke.fread.hosting.main_drawer_title
import com.zhangke.fread.profile.screen.setting.SettingScreen
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.statusui.ic_drag_indicator
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun Screen.MainDrawer(
    onDismissRequest: () -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    val screenVisitor = LocalModuleScreenVisitor.current
    val viewModel = getViewModel<MainDrawerViewModel>()
    val uiState by viewModel.uiState.collectAsState()
    val mainTabConnection = LocalNestedTabConnection.current
    val coroutineScope = rememberCoroutineScope()
    MainDrawerContent(
        uiState = uiState,
        onContentConfigClick = {
            onDismissRequest()
            coroutineScope.launch {
                mainTabConnection.scrollToContentTab(it)
            }
        },
        onAddContentClick = {
            onDismissRequest()
            navigator.push(PreAddFeedsScreen())
        },
        onMove = viewModel::onContentConfigMove,
        onEditClick = {
            onDismissRequest()
            viewModel.onContentConfigEditClick(it)
        },
        onSettingClick = {
            onDismissRequest()
            navigator.push(SettingScreen())
        },
        onDonateClick = {
            bottomSheetNavigator.show(screenVisitor.profileScreenVisitor.getDonateScreen())
        },
    )
    ConsumeOpenScreenFlow(viewModel.openScreenFlow)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainDrawerContent(
    uiState: MainDrawerUiState,
    onContentConfigClick: (FreadContent) -> Unit,
    onAddContentClick: () -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
    onEditClick: (FreadContent) -> Unit,
    onSettingClick: () -> Unit,
    onDonateClick: () -> Unit,
) {
    val contentConfigList = uiState.contentConfigList
    Surface(modifier = Modifier.fillMaxSize()) {
        if (contentConfigList.isEmpty()) {
            EmptyContent(Modifier.fillMaxSize(), onAddContentClick)
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopAppBar(
                    title = {
                        Text(text = stringResource(Res.string.main_drawer_title))
                    },
                    actions = {
                        SimpleIconButton(
                            onClick = onAddContentClick,
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Content",
                        )
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(16.dp))
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
                                val elevation by animateDpAsState(
                                    targetValue = if (dragging) 16.dp else 0.dp,
                                    label = "MainDrawerItemElevation",
                                )
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    shadowElevation = elevation,
                                ) {
                                    ContentConfigItem(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        contentConfig = contentConfig,
                                        onClick = { onContentConfigClick(contentConfig) },
                                        onEditClick = onEditClick,
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .clickable {
                            onSettingClick()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Settings",
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = stringResource(Res.string.main_drawer_settings))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clickable {
                            onDonateClick()
                        },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Outlined.Coffee,
                        contentDescription = "Donate",
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = stringResource(Res.string.main_drawer_donate))
                }
            }
        }
    }
}

@Composable
private fun ContentConfigItem(
    modifier: Modifier,
    contentConfig: FreadContent,
    onClick: () -> Unit,
    onEditClick: (FreadContent) -> Unit,
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1F)
                .padding(top = 16.dp, bottom = 6.dp, end = 4.dp),
        ) {
            Text(
                modifier = Modifier,
                text = contentConfig.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            contentConfig.Subtitle()
        }
        SimpleIconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp)
                .alpha(0.7F)
                .padding(2.dp),
            onClick = { onEditClick(contentConfig) },
            imageVector = Icons.Default.Settings,
            contentDescription = "Edit Content Config",
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp)
                .alpha(0.7F)
                .padding(2.dp),
            painter = painterResource(com.zhangke.fread.statusui.Res.drawable.ic_drag_indicator),
            contentDescription = "Drag for reorder Content Config",
        )
    }
}
