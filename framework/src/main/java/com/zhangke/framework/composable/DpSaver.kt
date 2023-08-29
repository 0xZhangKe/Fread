package com.zhangke.framework.composable

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.unit.Dp

object DpSaver {

    val Saver: Saver<Dp, *> = Saver(
        save = {
            it.value
        },
        restore = {
            Dp(it)
        }
    )
}
