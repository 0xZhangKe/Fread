package com.zhangke.fread.debug.screens.video

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.fread.commonbiz.shared.screen.FullVideoScreen

class FullVideoPlayDemoScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = {
                FullVideoScreen("https://media.cmx.edu.kg/media_attachments/files/112/782/745/301/676/929/original/78da2d1c7fd52d2f.mp4").let {
                    navigator.push(it)
                }
            }) {
                Text(text = "GO")
            }
        }
    }
}
