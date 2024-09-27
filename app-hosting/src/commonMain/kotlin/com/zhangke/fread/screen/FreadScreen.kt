package com.zhangke.fread.screen

import androidx.compose.runtime.Composable
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.screen.main.MainPage

class FreadScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        MainPage()
    }
}
