package com.zhangke.utopia.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.zhangke.framework.composable.GlobalScreenProvider
import com.zhangke.framework.composable.LocalGlobalScreenProvider
import com.zhangke.utopia.debug.screens.media.ImageMediaTestScreen
import com.zhangke.utopia.pages.main.MainPage

class UtopiaScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current!!
        val globalScreenProvider = remember {
            GlobalScreenProvider()
        }
        CompositionLocalProvider(
            LocalGlobalScreenProvider provides globalScreenProvider
        ) {
//            MainPage()
            LaunchedEffect(Unit){
                navigator.push(ImageMediaTestScreen())
            }
        }
        globalScreenProvider.content.value?.invoke()
    }
}

