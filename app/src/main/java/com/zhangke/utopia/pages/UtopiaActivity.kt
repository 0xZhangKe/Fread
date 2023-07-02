package com.zhangke.utopia.pages

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.utopia.pages.feeds.FeedsRouters
import com.zhangke.utopia.pages.feeds.registerFeedsNavigation
import com.zhangke.utopia.pages.main.MainRouter
import com.zhangke.utopia.pages.main.mainNavigation
import com.zhangke.utopia.pages.sources.registerSourcesNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UtopiaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UtopiaTheme {
                MainPage()
            }
        }
    }

    @Composable
    private fun MainPage() {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = MainRouter().route,
        ) {
            mainNavigation(navController)
        }
    }
}
