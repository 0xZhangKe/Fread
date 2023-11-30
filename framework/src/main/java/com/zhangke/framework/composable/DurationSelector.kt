package com.zhangke.framework.composable

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sd.lib.compose.wheel_picker.FVerticalWheelPicker
import com.sd.lib.compose.wheel_picker.rememberFWheelPickerState
import com.zhangke.utopia.framework.R
import kotlin.time.Duration

@Composable
fun DurationSelector(
    duration: Duration,
    onDismissRequest: () -> Unit,
    onDurationSelect: (Duration) -> Unit,
) {
    UtopiaDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.duration_selector_title),
        onNegativeClick = {
            onDismissRequest()
        },
        onPositiveClick = {
            onDismissRequest()
        },
        content = {
            DurationSelectorContent(
                onDurationSelect = {},
            )
        },
    )
}

@Composable
private fun DurationSelectorContent(
    onDurationSelect: (Duration) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1F)) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.duration_day),
            )
            val dayList = remember {
                mutableListOf("0", "1", "2", "3", "4", "5", "6", "7")
            }
            DurationSelectorItem(
                initialIndex = 1,
                modifier = Modifier.fillMaxWidth(),
                list = dayList,
                onSelect = {},
            )
        }
        Box(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1F)) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.duration_hour),
            )
            val hourList = remember {
                mutableListOf<String>().apply {
                    repeat(24) {
                        add("$it")
                    }
                }
            }
            DurationSelectorItem(
                initialIndex = 0,
                modifier = Modifier.fillMaxWidth(),
                list = hourList,
                onSelect = {},
            )
        }
        Box(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1F)) {
            Text(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = stringResource(R.string.duration_minute),
            )
            val minutesList = remember {
                mutableListOf<String>().apply {
                    repeat(60) {
                        add("$it")
                    }
                }
            }
            DurationSelectorItem(
                initialIndex = 0,
                modifier = Modifier.fillMaxWidth(),
                list = minutesList,
                onSelect = {},
            )
        }
    }
}

@Composable
private fun DurationSelectorItem(
    initialIndex: Int,
    modifier: Modifier = Modifier,
    list: List<String>,
    onSelect: (index: Int) -> Unit,
) {
    val state = rememberFWheelPickerState(initialIndex)
    val currentIndex = state.currentIndex
    LaunchedEffect(currentIndex) {
        Log.d("U_TEST", "current index:$currentIndex")
    }
    FVerticalWheelPicker(
        modifier = modifier,
        count = list.size,
        state = state,
    ) { index ->
        Text(text = list[index])
    }
}
