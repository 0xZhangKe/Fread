package com.zhangke.utopia.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.framework.voyager.ROOT_NAVIGATOR_KEY
import com.zhangke.framework.voyager.TransparentNavigator
import com.zhangke.utopia.common.daynight.DayNightHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UtopiaActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        DayNightHelper.setActivityDayNightMode()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val dayNightMode by DayNightHelper.dayNightModeFlow.collectAsState()
            UtopiaTheme(
                darkTheme = dayNightMode.isNight,
            ) {
                TransparentNavigator {
                    BottomSheetNavigator(
                        modifier = Modifier,
                        sheetShape = RoundedCornerShape(12.dp),
                    ) {
                        Navigator(
                            screen = UtopiaScreen(),
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
