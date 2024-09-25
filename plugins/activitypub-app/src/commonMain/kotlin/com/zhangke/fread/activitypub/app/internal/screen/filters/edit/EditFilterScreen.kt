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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.DatePickerDialog
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberFutureDatePickerState
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_back_dialog
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_context_selector_title
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_context_title
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_delete_content
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_duration
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_duration_custom
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_duration_one_day
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_duration_one_hour
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_duration_one_week
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_duration_permanent
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_duration_thirty_minutes
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_duration_three_day
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_duration_twelve_hours
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_empty_context
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_input_title_label
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_keyword_list_desc
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_keyword_list_title
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_title
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_warning_desc
import com.zhangke.fread.activitypub.app.activity_pub_filter_edit_warning_title
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class EditFilterScreen(
    private val role: IdentityRole,
    private val id: String?,
) : BaseScreen() {

    @OptIn(InternalVoyagerApi::class)
    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<EditFilterViewModel, EditFilterViewModel.Factory> {
            it.create(role, id)
        }
        val addedKeywordList by navigator.navigationResult
            .getResult<List<EditFilterUiState.Keyword>>(screenKey = HiddenKeywordScreen.SCREEN_KEY)
        LaunchedEffect(addedKeywordList) {
            addedKeywordList?.let(viewModel::onKeywordChanged)
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
                navigator.pop()
            }
        }

        BackHandler(true) {
            onBack()
        }

        EditFilterContent(
            uiState = uiState,
            snackBarHostState = snackBarHostState,
            onBackClick = {
                onBack()
            },
            onTitleChanged = viewModel::onTitleChanged,
            onExpiredDateSelected = viewModel::onExpiredDateSelected,
            onKeywordClick = {
                navigator.push(HiddenKeywordScreen(uiState.keywordList))
            },
            onContextChanged = viewModel::onContextChanged,
            onWarningCheckChanged = viewModel::onWarningCheckChanged,
            onDeleteClick = viewModel::onDeleteClick,
            onSubmitClick = viewModel::onSubmitClick,
        )
        ConsumeSnackbarFlow(hostState = snackBarHostState, messageTextFlow = viewModel.snackBarFlow)
        ConsumeFlow(viewModel.finishPageFlow) {
            navigator.pop()
        }

        if (showBackDialog) {
            FreadDialog(
                onDismissRequest = { showBackDialog = false },
                contentText = stringResource(Res.string.activity_pub_filter_edit_back_dialog),
                onNegativeClick = { showBackDialog = false },
                onPositiveClick = {
                    showBackDialog = false
                    navigator.pop()
                }
            )
        }
    }

    @Composable
    private fun EditFilterContent(
        uiState: EditFilterUiState,
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
                    title = stringResource(Res.string.activity_pub_filter_edit_title),
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
                        Text(text = stringResource(Res.string.activity_pub_filter_edit_input_title_label))
                    },
                    label = {
                        Text(text = stringResource(Res.string.activity_pub_filter_edit_input_title_label))
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
                    title = stringResource(Res.string.activity_pub_filter_edit_keyword_list_title),
                    subtitle = stringResource(
                        Res.string.activity_pub_filter_edit_keyword_list_desc,
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DurationItem(
        uiState: EditFilterUiState,
        onExpiredDateSelected: (Instant?) -> Unit,
    ) {
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp * 0.5F
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
                title = stringResource(Res.string.activity_pub_filter_edit_duration),
                subtitle = uiState.getExpiresDateDesc(),
            )
            DropdownMenu(
                offset = DpOffset(screenWidth, 0.dp),
                expanded = showDurationPopup,
                onDismissRequest = { showDurationPopup = false },
            ) {
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.activity_pub_filter_edit_duration_permanent)) },
                    onClick = {
                        showDurationPopup = false
                        onExpiredDateSelected(null)
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.activity_pub_filter_edit_duration_thirty_minutes)) },
                    onClick = {
                        showDurationPopup = false
                        onExpiredDateSelected(Clock.System.now().plus(30.minutes))
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.activity_pub_filter_edit_duration_one_hour)) },
                    onClick = {
                        showDurationPopup = false
                        onExpiredDateSelected(Clock.System.now().plus(1.hours))
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.activity_pub_filter_edit_duration_twelve_hours)) },
                    onClick = {
                        showDurationPopup = false
                        onExpiredDateSelected(Clock.System.now().plus(12.hours))
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.activity_pub_filter_edit_duration_one_day)) },
                    onClick = {
                        showDurationPopup = false
                        onExpiredDateSelected(Clock.System.now().plus(1.days))
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.activity_pub_filter_edit_duration_three_day)) },
                    onClick = {
                        showDurationPopup = false
                        onExpiredDateSelected(Clock.System.now().plus(3.days))
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.activity_pub_filter_edit_duration_one_week)) },
                    onClick = {
                        showDurationPopup = false
                        onExpiredDateSelected(Clock.System.now().plus(7.days))
                    },
                )
                DropdownMenuItem(
                    text = { Text(text = stringResource(Res.string.activity_pub_filter_edit_duration_custom)) },
                    onClick = {
                        showDurationPopup = false
                        showDurationSelector = true
                    },
                )
            }
            val pickerState = rememberFutureDatePickerState(
                initialSelectedDateMillis = uiState.expiresDate?.time
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
                title = stringResource(Res.string.activity_pub_filter_edit_warning_title),
                subtitle = stringResource(Res.string.activity_pub_filter_edit_warning_desc),
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
            title = stringResource(Res.string.activity_pub_filter_edit_context_title),
            subtitle = uiState.contextList.map { it.title }.joinToString().ifEmpty {
                stringResource(Res.string.activity_pub_filter_edit_empty_context)
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
            title = stringResource(Res.string.activity_pub_filter_edit_context_selector_title),
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
                contentText = stringResource(Res.string.activity_pub_filter_edit_delete_content),
                onDismissRequest = { showConfirmDialog = false },
                onNegativeClick = { showConfirmDialog = false },
                onPositiveClick = {
                    showConfirmDialog = false
                    onDeleteClick()
                },
            )
        }
    }
}
