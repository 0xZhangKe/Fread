package com.zhangke.framework.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.FWheelPickerState
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import com.zhangke.framework.utils.format
import com.zhangke.fread.localization.LocalizedString
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun DurationSelector(
    defaultDuration: Duration,
    onDismissRequest: () -> Unit,
    onDurationSelect: (Duration) -> Unit,
) {
    var currentDuration by remember {
        mutableStateOf(defaultDuration)
    }
    FreadDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(LocalizedString.durationSelectorTitle),
        onNegativeClick = {
            onDismissRequest()
        },
        onPositiveClick = {
            onDismissRequest()
            onDurationSelect(currentDuration)
        },
        content = {
            DurationSelectorContent(
                defaultDuration = defaultDuration,
                onDurationChanged = {
                    currentDuration = it
                }
            )
        },
    )
}

@Composable
private fun DurationSelectorContent(
    defaultDuration: Duration,
    onDurationChanged: (Duration) -> Unit,
) {
    val dayList = remember {
        mutableListOf(0, 1, 2, 3, 4, 5, 6, 7)
    }
    val dayState = rememberFWheelPickerState(0)
    val hourList = remember {
        mutableListOf<Int>().apply {
            repeat(24) {
                add(it)
            }
        }
    }
    val hourState = rememberFWheelPickerState(0)
    val minutesList = remember {
        mutableListOf<Int>().apply {
            repeat(60) {
                add(it)
            }
        }
    }
    val minutesState = rememberFWheelPickerState(0)
    val currentDay = dayState.currentIndex
    val currentHour = hourState.currentIndex
    val currentMinutes = minutesState.currentIndex
    LaunchedEffect(currentDay, currentHour, currentMinutes) {
        if (currentDay < 0 || currentHour < 0 || currentMinutes < 0) return@LaunchedEffect
        val currentDuration = dayList[currentDay].days +
                hourList[currentHour].hours +
                minutesList[currentMinutes].minutes
        onDurationChanged(currentDuration)
    }
    LaunchedEffect(defaultDuration) {
        launch {
            val formatted = defaultDuration.format()
            dayList.indexOf(formatted.days).takeIf { it >= 0 }?.let {
                dayState.animateScrollToIndex(it)
            }
            hourList.indexOf(formatted.hours).takeIf { it >= 0 }?.let {
                hourState.animateScrollToIndex(it)
            }
            minutesList.indexOf(formatted.minutes).takeIf { it >= 0 }?.let {
                minutesState.animateScrollToIndex(it)
            }
        }
    }
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1F)) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(LocalizedString.durationDay),
            )
            DurationSelectorItem(
                state = dayState,
                modifier = Modifier.fillMaxWidth(),
                list = dayList,
            )
        }
        Box(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1F)) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(LocalizedString.durationHour),
            )
            DurationSelectorItem(
                state = hourState,
                modifier = Modifier.fillMaxWidth(),
                list = hourList,
            )
        }
        Box(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1F)) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(LocalizedString.durationMinute),
            )
            DurationSelectorItem(
                state = minutesState,
                modifier = Modifier.fillMaxWidth(),
                list = minutesList,
            )
        }
    }
}

@Composable
private fun DurationSelectorItem(
    state: FWheelPickerState,
    modifier: Modifier = Modifier,
    list: List<Int>,
) {
    FVerticalWheelPicker(
        modifier = modifier,
        count = list.size,
        state = state,
    ) { index ->
        Text(text = list[index].toString())
    }
}
