package com.zhangke.utopia.pages

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import com.zhangke.framework.architect.theme.UtopiaTheme
import com.zhangke.framework.voyager.TransparentNavigator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UtopiaActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UtopiaTheme {
                TransparentNavigator {
                    BottomSheetNavigator(
                        sheetShape = RoundedCornerShape(12.dp),
                    ) {
                        Navigator(UtopiaScreen())
                    }
                }
            }
        }
    }
}
