package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import cafe.adriel.voyager.core.screen.Screen

interface PagerTab {

    val options: PagerTabOptions?
        @Composable get

    @Composable
    fun Screen.TabContent()
}

data class PagerTabOptions(
    val title: String,
    val icon: Painter? = null
)
