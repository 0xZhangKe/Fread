package com.zhangke.fread.screen.main.drawer

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.fread.R
import com.zhangke.fread.analytics.MainDrawerElements
import com.zhangke.fread.analytics.reportClick
import com.zhangke.fread.feeds.pages.home.EmptyContent
import com.zhangke.fread.feeds.pages.manager.add.pre.PreAddFeedsScreen
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.ic_drag_indicator
import com.zhangke.fread.statusui.ic_mode_edit
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun Screen.MainDrawer(
    onDismissRequest: () -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel = getScreenModel<MainDrawerScreenModel>()
    val uiState by viewModel.uiState.collectAsState()
    val mainTabConnection = LocalNestedTabConnection.current
    val coroutineScope = rememberCoroutineScope()
    MainDrawerContent(
        uiState = uiState,
        onContentConfigClick = {
            onDismissRequest()
            reportClick(MainDrawerElements.CONTENT)
            coroutineScope.launch {
                mainTabConnection.scrollToContentTab(it)
            }
        },
        onAddContentClick = {
            onDismissRequest()
            reportClick(MainDrawerElements.ADD_CONTENT)
            navigator.push(PreAddFeedsScreen())
        },
        onMove = viewModel::onContentConfigMove,
        onEditClick = {
            onDismissRequest()
            reportClick(MainDrawerElements.ITEM_EDIT)
            viewModel.onContentConfigEditClick(it)
        },
    )
    ConsumeOpenScreenFlow(viewModel.openScreenFlow)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainDrawerContent(
    uiState: MainDrawerUiState,
    onContentConfigClick: (ContentConfig) -> Unit,
    onAddContentClick: () -> Unit,
    onMove: (from: Int, to: Int) -> Unit,
    onEditClick: (ContentConfig) -> Unit,
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
                        Text(text = stringResource(R.string.main_drawer_title))
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
                text = contentConfig.configName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            val context = LocalContext.current
            when (contentConfig) {
                is ContentConfig.ActivityPubContent -> {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        Image(
                            modifier = Modifier.size(14.dp),
                            painter = painterResource(com.zhangke.fread.commonbiz.R.drawable.mastodon_logo),
                            contentDescription = null,
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .align(Alignment.Bottom),
                            text = contentConfig.baseUrl.host,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                is ContentConfig.MixedContent -> {
                    Text(
                        modifier = Modifier,
                        text = buildSubtitle(context, contentConfig),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        SimpleIconButton(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp)
                .alpha(0.7F)
                .padding(2.dp),
            onClick = { onEditClick(contentConfig) },
            painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_mode_edit),
            contentDescription = "Edit Content Config",
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .size(24.dp)
                .alpha(0.7F)
                .padding(2.dp),
            painter = org.jetbrains.compose.resources.painterResource(Res.drawable.ic_drag_indicator),
            contentDescription = "Edit Content Config",
        )
    }
}

private fun buildSubtitle(
    context: Context,
    config: ContentConfig.MixedContent,
): AnnotatedString {
    return buildAnnotatedString {
        val prefix = context.getString(R.string.main_drawer_mixed_item_subtitle_1)
        val suffix = context.getString(R.string.main_drawer_mixed_item_subtitle_2)
        append(prefix)
        val sizeString = " " + config.sourceUriList.size.toString() + " "
        append(sizeString)
        addStyle(
            style = SpanStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            ),
            start = prefix.length,
            end = prefix.length + sizeString.length,
        )
        append(suffix)
    }
}
