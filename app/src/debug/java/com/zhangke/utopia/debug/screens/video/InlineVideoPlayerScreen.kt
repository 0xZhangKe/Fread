package com.zhangke.utopia.debug.screens.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.utopia.status.ui.video.inline.InlineVideo

class InlineVideoPlayerScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current!!
        Scaffold(
            topBar = {
                Toolbar(
                    title = "InlineVideo",
                    onBackClick = navigator::pop,
                )
            }
        ) { paddingValues ->
            val list = mutableListOf<Int>()
            repeat(100) {
                list += it
            }
//            Box(modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)) {
//                AsyncImage(
//                    modifier = Modifier.size(60.dp),
//                    model = "https://pbs.twimg.com/media/F8ZjbTDakAAOCfA?format=jpg&name=small",
//                    contentScale = ContentScale.Crop,
//                    contentDescription = "123",
//                )
//            }

            InlineVideoLazyColumn(
                modifier = Modifier.padding(paddingValues),
            ) {
                itemsIndexed(list) { index, item ->
                    if (item == 5 || item == 3) {
                        InlineVideoItem(
                            url = if (item == 3) {
                                "https://media.cmx.edu.kg/cache/media_attachments/files/111/318/410/597/746/411/original/b9b3e11728fc6bf9.mp4"
                            } else {
                                "https://video.twimg.com/ext_tw_video/1712110948700352512/pu/vid/avc1/720x1280/i43wruptl2R9KHAZ.mp4?tag=12"
                            },
                            index = index,
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .fillMaxWidth()
                                .height(80.dp),
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shadowElevation = 6.dp,
                                color = Color.Blue,
                            ) {
                                var progress by remember {
                                    mutableFloatStateOf(0.3F)
                                }
                                Slider(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = progress,
                                    onValueChange = { progress = it },
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color.White,
                                        activeTrackColor = Color.White,
                                        activeTickColor = Color.White,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun InlineVideoItem(
        url: String,
        index: Int,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 6.dp,
            ) {
                InlineVideo(
                    aspectRatio = 2F,
                    indexInList = index,
                    coverImage = "https://pbs.twimg.com/media/F8ZjbTDakAAOCfA?format=jpg&name=small",
                    uri = url.toUri(),
                    onClick = {

                    },
                )
            }
        }
    }
}
