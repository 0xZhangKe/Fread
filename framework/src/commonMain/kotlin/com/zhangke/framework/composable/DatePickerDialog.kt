package com.zhangke.framework.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.localization.Res
import com.zhangke.fread.localization.ok
import io.ktor.util.date.getTimeMillis
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    datePickerState: DatePickerState,
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    if (!visible) return
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = {
            coroutineScope.launch {
                bottomSheetState.hide()
                onDismissRequest()
            }
        },
        dragHandle = null,
    ) {
        Column(
            modifier = Modifier
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = {
                    onDismissRequest()
                    onConfirmClick()
                }) {
                    Text(text = stringResource(Res.string.ok))
                }
            }
            DatePicker(
                state = datePickerState,
                showModeToggle = false,
                colors = DatePickerDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberFutureDatePickerState(
    initialSelectedDateMillis: Long? = null,
): DatePickerState {
    val currentYear = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
    }
    return rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        yearRange = currentYear..2100,
        selectableDates = remember {
            FutureDates()
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
class FutureDates : SelectableDates {

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return getTimeMillis() < utcTimeMillis
    }
}
