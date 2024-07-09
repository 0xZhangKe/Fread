package com.zhangke.fread.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.zhangke.fread.common.browser.BrowserBridgeDialogActivity
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.screen.main.MainPage

class FreadScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
//        MainPage()
        val context = LocalContext.current
        Box(modifier = Modifier.fillMaxSize()){
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    BrowserBridgeDialogActivity.open(context, "https://stackoverflow.com/questions/4605527/converting-pixels-to-dp")
                }) {
                Text(text = "GOGOGO")
            }
        }
    }
}
