package com.zhangke.utopia.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.framework.voyager.ROOT_NAVIGATOR_KEY
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.utopia.common.daynight.DayNightHelper
import com.zhangke.utopia.debug.screens.collapsable.CollapsableDemoScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UtopiaActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        DayNightHelper.setActivityDayNightMode()
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            val dayNightMode by DayNightHelper.dayNightModeFlow.collectAsState()
            UtopiaTheme(
                useDarkTheme = dayNightMode.isNight,
            ) {
                TransparentNavigator {
                    BottomSheetNavigator(
                        modifier = Modifier.navigationBarsPadding(),
                        sheetShape = RoundedCornerShape(12.dp),
                    ) {
                        Navigator(
//                            screen = UtopiaScreen(),
                            screen = CollapsableDemoScreen(),
                            key = ROOT_NAVIGATOR_KEY,
                        )
                    }
                }
            }
        }
    }

    override fun onNightModeChanged(mode: Int) {
        super.onNightModeChanged(mode)
        recreate()
    }
}
