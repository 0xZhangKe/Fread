package com.zhangke.fread.activitypub.app.internal.screen.filters.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.common.page.BaseScreen

class EditFilterScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
    }

    @Composable
    private fun EditFilterContent(
        uiState: EditFilterUiState,
        snackBarHostState: SnackbarHostState,
        onBackClick: () -> Unit,
        onTitleChanged: (String) -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(R.string.activity_pub_filter_edit_title),
                    onBackClick = onBackClick,
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
                        Text(text = stringResource(R.string.activity_pub_filter_edit_input_title_label))
                    },
                    label = {
                        Text(text = stringResource(R.string.activity_pub_filter_edit_input_title_label))
                    },
                )
            }

        }
    }

    @Composable
    private fun DurationItem(

    ) {
        var showDurationPopup by remember {
            mutableStateOf(false)
        }
        var showDurationSelector by remember {
            mutableStateOf(false)
        }
        LinedItem(
            modifier = Modifier
                .clickable {
                    showDurationPopup = true
                }
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            title = stringResource(id = R.string.activity_pub_filter_edit_duration),
            subtitle = "",
        )
        DropdownMenu(
            expanded = showDurationPopup,
            onDismissRequest = { showDurationPopup = false },
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.activity_pub_filter_edit_duration_permanent)) },
                onClick = {
                    showDurationPopup = false
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.activity_pub_filter_edit_duration_thirty_minutes)) },
                onClick = {
                    showDurationPopup = false
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.activity_pub_filter_edit_duration_one_hour)) },
                onClick = {
                    showDurationPopup = false
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.activity_pub_filter_edit_duration_twelve_hours)) },
                onClick = {
                    showDurationPopup = false
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.activity_pub_filter_edit_duration_one_day)) },
                onClick = {
                    showDurationPopup = false
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.activity_pub_filter_edit_duration_three_day)) },
                onClick = {
                    showDurationPopup = false
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.activity_pub_filter_edit_duration_one_week)) },
                onClick = {
                    showDurationPopup = false
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.activity_pub_filter_edit_duration_custom)) },
                onClick = {
                    showDurationPopup = false
                    showDurationSelector = true
                },
            )
        }

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
}
