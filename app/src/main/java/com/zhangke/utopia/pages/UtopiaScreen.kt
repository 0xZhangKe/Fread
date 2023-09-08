package com.zhangke.utopia.pages

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.androidx.AndroidScreen
import com.zhangke.utopia.pages.main.MainPage

class UtopiaScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        MainPage()
    }
}
