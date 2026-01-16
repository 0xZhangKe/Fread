package com.zhangke.fread.activitypub.app.internal.screen.filters.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.DatePickerDialog
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.rememberFutureDatePickerState
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.utils.getScreenWidth
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class EditFilterScreenKey(
    val locator: PlatformLocator,
    val id: String? = null,
) : NavKey

@OptIn(ExperimentalComposeUiApi::class, ExperimentalTime::class)
@Composable
fun EditFilterScreen(viewModel: EditFilterViewModel, id: String?) {
    val backStack = LocalNavBackStack.currentOrThrow
    ConsumeFlow(HiddenKeywordScreenNavKey.keywordsListFlow.flow) {
        viewModel.onKeywordChanged(it)
    }
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = rememberSnackbarHostState()

    var showBackDialog by remember {
        mutableStateOf(false)
    }

    fun onBack() {
        if (uiState.hasInputtedSomething) {
            showBackDialog = true
        } else {
            backStack.removeLastOrNull()
        }
    }

    BackHandler(true) { onBack() }

    EditFilterContent(
        uiState = uiState,
        id = id,
        snackBarHostState = snackBarHostState,
        onBackClick = { onBack() },
        onTitleChanged = viewModel::onTitleChanged,
        onExpiredDateSelected = viewModel::onExpiredDateSelected,
        onKeywordClick = {
            backStack.add(HiddenKeywordScreenNavKey(uiState.keywordList))
        },
        onContextChanged = viewModel::onContextChanged,
        onWarningCheckChanged = viewModel::onWarningCheckChanged,
        onDeleteClick = viewModel::onDeleteClick,
        onSubmitClick = viewModel::onSubmitClick,
    )
    ConsumeSnackbarFlow(hostState = snackBarHostState, messageTextFlow = viewModel.snackBarFlow)
    ConsumeFlow(viewModel.finishPageFlow) {
        backStack.removeLastOrNull()
    }

    if (showBackDialog) {
        FreadDialog(
            onDismissRequest = { showBackDialog = false },
            contentText = stringResource(LocalizedString.activity_pub_filter_edit_back_dialog),
            onNegativeClick = { showBackDialog = false },
            onPositiveClick = {
                showBackDialog = false
                backStack.removeLastOrNull()
            }
        )
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun EditFilterContent(
    uiState: EditFilterUiState,
    id: String?,
    snackBarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onExpiredDateSelected: (Instant?) -> Unit,
    onKeywordClick: () -> Unit,
    onContextChanged: (List<FilterContext>) -> Unit,
    onWarningCheckChanged: (Boolean) -> Unit,
    onDeleteClick: () -> Unit,
    onSubmitClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Toolbar(
                title = stringResource(LocalizedString.activity_pub_filter_edit_title),
                onBackClick = onBackClick,
                actions = {
                    if (id.isNullOrEmpty().not()) {
                        DeleteMenuItem(onDeleteClick = onDeleteClick)
                    }
                    SimpleIconButton(
                        onClick = onSubmitClick,
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                    )
                },
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 24.dp, end = 16.dp),
                value = uiState.title,
                onValueChange = onTitleChanged,
                maxLines = 1,
                placeholder = {
                    Text(text = stringResource(LocalizedString.activity_pub_filter_edit_input_title_label))
                },
                label = {
                    Text(text = stringResource(LocalizedString.activity_pub_filter_edit_input_title_label))
                },
            )

            DurationItem(
                uiState = uiState,
                onExpiredDateSelected = onExpiredDateSelected,
            )

            LinedItem(
                modifier = Modifier
                    .clickable {
                        onKeywordClick()
                    }
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                title = stringResource(LocalizedString.activity_pub_filter_edit_keyword_list_title),
                subtitle = stringResource(
                    LocalizedString.activity_pub_filter_edit_keyword_list_desc,
                    uiState.keywordCount
                ),
            )
            ContextItem(
                uiState = uiState,
                onContextChanged = onContextChanged,
            )
            WarningItem(
                uiState = uiState,
                onCheckChanged = onWarningCheckChanged,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
private fun DurationItem(
    uiState: EditFilterUiState,
    onExpiredDateSelected: (Instant?) -> Unit,
) {
    val screenWidth = getScreenWidth() * 0.5F
    var showDurationPopup by remember {
        mutableStateOf(false)
    }
    var showDurationSelector by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        LinedItem(
            modifier = Modifier
                .clickable {
                    showDurationPopup = true
                }
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            title = stringResource(LocalizedString.activity_pub_filter_edit_duration),
            subtitle = uiState.getExpiresDateDesc(),
        )
        DropdownMenu(
            offset = DpOffset(screenWidth, 0.dp),
            expanded = showDurationPopup,
            onDismissRequest = { showDurationPopup = false },
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(LocalizedString.activity_pub_filter_edit_duration_permanent)) },
                onClick = {
                    showDurationPopup = false
                    onExpiredDateSelected(null)
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(LocalizedString.activity_pub_filter_edit_duration_thirty_minutes)) },
                onClick = {
                    showDurationPopup = false
                    onExpiredDateSelected(Clock.System.now().plus(30.minutes))
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(LocalizedString.activity_pub_filter_edit_duration_one_hour)) },
                onClick = {
                    showDurationPopup = false
                    onExpiredDateSelected(Clock.System.now().plus(1.hours))
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(LocalizedString.activity_pub_filter_edit_duration_twelve_hours)) },
                onClick = {
                    showDurationPopup = false
                    onExpiredDateSelected(Clock.System.now().plus(12.hours))
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(LocalizedString.activity_pub_filter_edit_duration_one_day)) },
                onClick = {
                    showDurationPopup = false
                    onExpiredDateSelected(Clock.System.now().plus(1.days))
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(LocalizedString.activity_pub_filter_edit_duration_three_day)) },
                onClick = {
                    showDurationPopup = false
                    onExpiredDateSelected(Clock.System.now().plus(3.days))
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(LocalizedString.activity_pub_filter_edit_duration_one_week)) },
                onClick = {
                    showDurationPopup = false
                    onExpiredDateSelected(Clock.System.now().plus(7.days))
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(LocalizedString.activity_pub_filter_edit_duration_custom)) },
                onClick = {
                    showDurationPopup = false
                    showDurationSelector = true
                },
            )
        }
        val pickerState = rememberFutureDatePickerState(
            initialSelectedDateMillis = uiState.expiresDate?.toEpochMilliseconds()
        )
        DatePickerDialog(
            datePickerState = pickerState,
            visible = showDurationSelector,
            onDismissRequest = { showDurationSelector = false },
            onConfirmClick = {
                showDurationSelector = false
                pickerState.selectedDateMillis
                    ?.let { Instant.fromEpochMilliseconds(it) }
                    ?.let(onExpiredDateSelected)
            },
        )
    }
}

@Composable
private fun WarningItem(
    uiState: EditFilterUiState,
    onCheckChanged: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LinedItem(
            modifier = Modifier.weight(1F),
            title = stringResource(LocalizedString.activity_pub_filter_edit_warning_title),
            subtitle = stringResource(LocalizedString.activity_pub_filter_edit_warning_desc),
        )
        Switch(
            checked = uiState.filterByWarn,
            onCheckedChange = {
                onCheckChanged(it)
            },
        )
    }
}

@Composable
private fun ContextItem(
    uiState: EditFilterUiState,
    onContextChanged: (List<FilterContext>) -> Unit,
) {
    var showSelector by remember {
        mutableStateOf(false)
    }
    LinedItem(
        modifier = Modifier
            .clickable { showSelector = true }
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        title = stringResource(LocalizedString.activity_pub_filter_edit_context_title),
        subtitle = uiState.contextList.map { it.title }.joinToString().ifEmpty {
            stringResource(LocalizedString.activity_pub_filter_edit_empty_context)
        },
    )
    if (showSelector) {
        ContextSelector(
            selectedContext = uiState.contextList,
            onContextSelected = onContextChanged,
            onDismissRequest = { showSelector = false },
        )
    }
}

@Composable
private fun ContextSelector(
    selectedContext: List<FilterContext>,
    onContextSelected: (List<FilterContext>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val currentSelected = remember(selectedContext) {
        mutableStateListOf<FilterContext>().also { it.addAll(selectedContext) }
    }
    FreadDialog(
        title = stringResource(LocalizedString.activity_pub_filter_edit_context_selector_title),
        onDismissRequest = onDismissRequest,
        onNegativeClick = onDismissRequest,
        content = {
            Column {
                FilterContext.entries.forEach { context ->
                    val selected = currentSelected.contains(context)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterStart),
                            text = context.title,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Checkbox(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            checked = selected,
                            onCheckedChange = {
                                if (it) {
                                    currentSelected += context
                                } else {
                                    currentSelected -= context
                                }
                            },
                        )
                    }
                }
            }
        },
        onPositiveClick = {
            onDismissRequest()
            onContextSelected(currentSelected)
        },
    )
}

@Composable
private fun LinedItem(
    modifier: Modifier,
    title: String,
    subtitle: String,
) {
    Column(
        modifier = modifier,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DeleteMenuItem(onDeleteClick: () -> Unit) {
    var showConfirmDialog by remember {
        mutableStateOf(false)
    }
    SimpleIconButton(
        onClick = { showConfirmDialog = true },
        imageVector = Icons.Default.Delete,
        contentDescription = "Delete"
    )
    if (showConfirmDialog) {
        FreadDialog(
            contentText = stringResource(LocalizedString.activity_pub_filter_edit_delete_content),
            onDismissRequest = { showConfirmDialog = false },
            onNegativeClick = { showConfirmDialog = false },
            onPositiveClick = {
                showConfirmDialog = false
                onDeleteClick()
            },
        )
    }
}
