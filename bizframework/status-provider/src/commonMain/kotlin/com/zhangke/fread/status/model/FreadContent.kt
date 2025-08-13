package com.zhangke.fread.status.model

import androidx.compose.runtime.Composable
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.uri.FormalUri

interface FreadContent {

    val id: String

    val order: Int

    val name: String

    val accountUri: FormalUri?

    fun newOrder(newOrder: Int): FreadContent

    @Composable
    fun Subtitle(account: LoggedAccount?)
}
