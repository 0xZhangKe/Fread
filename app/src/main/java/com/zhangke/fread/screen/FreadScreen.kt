package com.zhangke.fread.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.screen.main.MainPage

class FreadScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        MainPage()

//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(Color.Black)
//        ) {
//
//            Box(
//                modifier = Modifier
//                    .background(Color.White)
//                    .align(Alignment.Center)
//                    .size(200.dp)
//                    .drawBehind {
//                        drawLine(
//                            Color.Red,
//                            start = Offset(x = 30F, y = 0F),
//                            end = Offset(x = 60F, y = size.height)
//                        )
//                    }
//            )
//        }
    }
}
