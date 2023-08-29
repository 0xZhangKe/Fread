package com.zhangke.framework.composable

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.unit.Dp

object StateSaver {

    val MutableNullableDpSaver: Saver<MutableState<Dp?>, *> = Saver(
        save = {
            it.value?.value
        },
        restore = {
            mutableStateOf(Dp(it))
        }
    )

    val MutableDpSaver: Saver<MutableState<Dp>, *> = Saver(
        save = {
            it.value.value
        },
        restore = {
            mutableStateOf(Dp(it))
        }
    )
}
