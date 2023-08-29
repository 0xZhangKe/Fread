package com.zhangke.utopia.commonbiz.shared.screen

import androidx.compose.animation.core.animateValueAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import cafe.adriel.voyager.androidx.AndroidScreen
import coil.compose.AsyncImage
import com.zhangke.framework.composable.offset
import com.zhangke.framework.composable.size
import com.zhangke.utopia.status.blog.BlogMedia

class ImageGalleryScreen(
    private val media: BlogMedia,
    private val coordinates: LayoutCoordinates,
) : AndroidScreen() {

    @Composable
    override fun Content() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.9F))
        ) {
            val originSize = coordinates.size
            val offset = coordinates.positionInRoot()
            val config = LocalConfiguration.current
            val screenWidth = config.screenWidthDp
            val screenHeight = config.screenHeightDp
//            var offsetX by animateValueAsState(targetValue = 0, typeConverter = )
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(originSize.width.toFloat() / originSize.height.toFloat())
                    .offset(offset)
                    .scrollable(rememberScrollState(), Orientation.Vertical),
                model = media.url,
                contentScale = ContentScale.Crop,
                contentDescription = media.description,
            )
        }
    }
}
