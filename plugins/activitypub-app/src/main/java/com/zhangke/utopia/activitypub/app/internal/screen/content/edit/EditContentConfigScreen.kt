package com.zhangke.utopia.activitypub.app.internal.screen.content.edit

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.utopiaPlaceholder
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.composable.tabName
import com.zhangke.utopia.status.model.ContentConfig
import kotlinx.coroutines.flow.Flow
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Destination(EditContentConfigRoute.ROUTE)
class EditContentConfigScreen(
    @Router private val route: String = ""
) : Screen {

    companion object {

        private val TAB_ITEM_HEIGHT = 56.dp
    }

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            getViewModel<EditContentConfigViewModel, EditContentConfigViewModel.Factory> {
                it.create(EditContentConfigRoute.parseRoute(route))
            }
        val uiState by viewModel.uiState.collectAsState()
        EditContentConfigScreenContent(
            uiState = uiState,
            snackbarMessageFlow = viewModel.snackbarMessageFlow,
            onBackClick = navigator::pop,
            onShowingTabMove = viewModel::onShowingTabMove,
            onShowingTabMoveDown = viewModel::onShowingTabMoveDown,
            onHiddenTabMoveUp = viewModel::onHiddenTabMoveUp,
        )
    }

    @Composable
    private fun EditContentConfigScreenContent(
        uiState: EditContentConfigUiState?,
        snackbarMessageFlow: Flow<TextString>,
        onBackClick: () -> Unit,
        onShowingTabMove: (from: Int, to: Int) -> Unit,
        onShowingTabMoveDown: (ContentConfig.ActivityPubContent.ContentTab) -> Unit,
        onHiddenTabMoveUp: (ContentConfig.ActivityPubContent.ContentTab) -> Unit,
    ) {
        val snackbarHostState = rememberSnackbarHostState()
        ConsumeSnackbarFlow(snackbarHostState, snackbarMessageFlow)
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            topBar = {
                Toolbar(
                    title = uiState?.config?.configName.orEmpty(),
                    onBackClick = onBackClick,
                )
            }
        ) { innerPaddings ->
            Column(
                modifier = Modifier
                    .padding(innerPaddings)
                    .fillMaxSize()
                    .utopiaPlaceholder(uiState == null)
                    .verticalScroll(rememberScrollState())
            ) {
                if (uiState != null) {
                    ShowingUserList(
                        uiState = uiState,
                        onShowingTabMove = onShowingTabMove,
                        onMoveDown = onShowingTabMoveDown,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    HiddenUserList(
                        uiState = uiState,
                        onMoveUp = onHiddenTabMoveUp,
                    )
                }
            }
        }
    }

    @Composable
    private fun ShowingUserList(
        uiState: EditContentConfigUiState,
        onShowingTabMove: (from: Int, to: Int) -> Unit,
        onMoveDown: (ContentConfig.ActivityPubContent.ContentTab) -> Unit,
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            text = stringResource(R.string.activity_pub_edit_content_config_showing_list_title),
            style = MaterialTheme.typography.titleMedium,
        )
        var tabsInUi by remember(uiState.config.showingTabList) {
            mutableStateOf(uiState.config.showingTabList)
        }
        key(uiState.config.showingTabList) {
            val state = rememberReorderableLazyListState(
                onMove = { from, to ->
                    if (tabsInUi.isEmpty()) return@rememberReorderableLazyListState
                    tabsInUi = tabsInUi.toMutableList().apply {
                        add(to.index, removeAt(from.index))
                    }
                },
                onDragEnd = { startIndex, endIndex ->
                    onShowingTabMove(startIndex, endIndex)
                }
            )
            LazyColumn(
                state = state.listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TAB_ITEM_HEIGHT * tabsInUi.size + 4.dp)
                    .reorderable(state)
                    .detectReorderAfterLongPress(state)
            ) {
                itemsIndexed(
                    items = tabsInUi,
                    key = { _, item -> item.hashCode() }
                ) { _, tabItem ->
                    ReorderableItem(state, tabItem.hashCode()) { dragging ->
                        val elevation by animateDpAsState(if (dragging) 16.dp else 0.dp, label = "")
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(TAB_ITEM_HEIGHT),
                            shadowElevation = elevation,
                            tonalElevation = elevation,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 16.dp),
                                    text = tabItem.tabName(),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Spacer(modifier = Modifier.weight(1F))
                                SimpleIconButton(
                                    onClick = { onMoveDown(tabItem) },
                                    imageVector = Icons.Default.VisibilityOff,
                                    contentDescription = "Move Down",
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = null,
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun HiddenUserList(
        uiState: EditContentConfigUiState,
        onMoveUp: (ContentConfig.ActivityPubContent.ContentTab) -> Unit,
    ) {
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            text = stringResource(R.string.activity_pub_edit_content_config_hidden_list_title),
            style = MaterialTheme.typography.titleMedium,
        )
        uiState.config.hiddenTabList.forEach { tabItem ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(TAB_ITEM_HEIGHT),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Text(
                        modifier = Modifier.padding(start = 16.dp),
                        text = tabItem.tabName(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.weight(1F))
                    SimpleIconButton(
                        onClick = { onMoveUp(tabItem) },
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Move Up",
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        }
    }
}
