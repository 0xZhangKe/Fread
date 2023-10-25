package com.zhangke.utopia.debug.screens.video

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.composable.inline.LocalPlayableIndexRecorder
import com.zhangke.framework.composable.sensitive.SensitiveLazyColumn
import com.zhangke.framework.composable.sensitive.SensitiveLazyColumnState
import com.zhangke.utopia.status.ui.video.InlineVideo
import kotlin.math.max

class InlineVideoPlayerScreen : AndroidScreen() {

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
                    if (item == 5) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            InlineVideo(
                                aspectRatio = 2F,
                                coverImage = "https://pbs.twimg.com/media/F8ZjbTDakAAOCfA?format=jpg&name=small",
                                uri = "https://video.twimg.com/ext_tw_video/1712110948700352512/pu/vid/avc1/720x1280/i43wruptl2R9KHAZ.mp4?tag=12".toUri(),
                            )
                            LocalPlayableIndexRecorder.current?.recordePlayableIndex(index)
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 16.dp)
                                .fillMaxWidth()
                                .height(80.dp)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shadowElevation = 6.dp,
                            ) {
                                Text(text = "-----------$item----------")
                            }
                        }
                    }
                }
            }
        }
    }
}
