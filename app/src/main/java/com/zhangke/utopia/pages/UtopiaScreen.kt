package com.zhangke.utopia.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.SearchToolbar
import com.zhangke.framework.composable.Toolbar
import com.zhangke.utopia.pages.main.MainPage

class UtopiaScreen : AndroidScreen() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        MainPage()
//        val navigator = LocalNavigator.currentOrThrow
//        LaunchedEffect(Unit) {
//            navigator.push(InlineVideoPlayerScreen())
//            navigator.push(FullVideoScreen(
//                "https://video.twimg.com/ext_tw_video/1712110948700352512/pu/vid/avc1/720x1280/i43wruptl2R9KHAZ.mp4?tag=12".toUri()
//            ))
//        }

//        Scaffold(
//            topBar = {
//                SearchToolbar (
//                    onBackClick = {
//
//                    }
//                )
//            },
//        ) {
//            Box(modifier = Modifier.padding(it)) {
//                Text(text = "Content")
//            }
//        }
    }
}
