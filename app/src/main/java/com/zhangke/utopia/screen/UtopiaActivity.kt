package com.zhangke.utopia.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.framework.voyager.ROOT_NAVIGATOR_KEY
import com.zhangke.framework.voyager.TransparentNavigator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UtopiaActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, true)

        setContent {
            UtopiaTheme {
                TransparentNavigator {
                    BottomSheetNavigator(
                        modifier = Modifier.navigationBarsPadding(),
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
}
