package com.zhangke.fread.status.model

import androidx.compose.runtime.Composable

interface FreadContent {

    val id: String

    val order: Int

    val name: String

    fun newOrder(newOrder: Int): FreadContent

    @Composable
    fun Subtitle()
}
