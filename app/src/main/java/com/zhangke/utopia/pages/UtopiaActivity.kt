package com.zhangke.utopia.pages

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.framework.composable.GlobalScreenProvider
import com.zhangke.framework.composable.LocalGlobalScreenProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UtopiaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UtopiaTheme {
                val globalScreenProvider = remember {
                    GlobalScreenProvider()
                }
                CompositionLocalProvider(
                    LocalGlobalScreenProvider provides globalScreenProvider
                ) {
                    Navigator(UtopiaScreen())
                }
                globalScreenProvider.content.value?.invoke()
            }
        }
    }
}
