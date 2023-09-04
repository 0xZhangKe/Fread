package com.zhangke.utopia.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import coil.compose.AsyncImage
import com.zhangke.framework.voyager.LocalTransparentNavigator
import com.zhangke.utopia.commonbiz.shared.screen.ImageGalleryScreen
import com.zhangke.utopia.pages.main.MainPage
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogMediaType

class UtopiaScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        MainPage()
//        Box(modifier = Modifier.fillMaxSize()) {
//            val transparentNavigator = LocalTransparentNavigator.current
//            val media = BlogMedia(
//                id = "",
//                url = "https://media.cmx.edu.kg/cache/media_attachments/files/110/994/199/046/134/697/original/a672a13d98c21627.jpg",
//                type = BlogMediaType.IMAGE,
//                previewUrl = null,
//                remoteUrl = null,
//                description = null,
//                blurhash = null,
//                meta = null,
//            )
//            val coordinates by remember {
//                mutableStateOf(arrayOfNulls<LayoutCoordinates>(1))
//            }
//            AsyncImage(
//                modifier = Modifier
//                    .width(200.dp)
//                    .height(180.dp)
//                    .onGloballyPositioned {
//                        coordinates[0] = it
//                    }
//                    .clickable {
//                        transparentNavigator.push(
//                            ImageGalleryScreen(
//                                selectedIndex = 0,
//                                mediaList = listOf(media),
//                                coordinatesList = coordinates.toList(),
//                                onDismiss = {},
//                            )
//                        )
//                    },
//                model = media.url,
//                contentDescription = ""
//            )
//        }
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//        ) {
//            Box(
//                modifier = Modifier
//                    .align(Alignment.Center)
//                    .horizontalScroll(rememberScrollState())
//                    .verticalScroll(rememberScrollState())
//            ) {
//                AsyncImage(
//                    modifier = Modifier
//                        .width(1000.dp)
//                        .height(1000.dp),
//                    contentScale = ContentScale.FillBounds,
//                    model = "https://media.cmx.edu.kg/cache/media_attachments/files/110/994/199/046/134/697/original/a672a13d98c21627.jpg",
//                    contentDescription = "",
//                )
//            }
//        }
    }
}
