package com.zhangke.utopia.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import cafe.adriel.voyager.androidx.AndroidScreen
import com.zhangke.framework.composable.GlobalScreenProvider
import com.zhangke.framework.composable.LocalGlobalScreenProvider
import com.zhangke.utopia.pages.main.MainPage

class UtopiaScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val globalScreenProvider = remember {
            GlobalScreenProvider()
        }
        CompositionLocalProvider(
            LocalGlobalScreenProvider provides globalScreenProvider
        ) {
//            MainPage()
            MediaTestPage()
        }
        globalScreenProvider.content.value?.invoke()
    }
}

@Composable
private fun MediaTestPage(){

}
