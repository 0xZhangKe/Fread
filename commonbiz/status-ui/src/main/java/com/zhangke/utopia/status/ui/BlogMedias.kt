package com.zhangke.utopia.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.zhangke.framework.utils.pxToDp
import com.zhangke.utopia.status.blog.BlogMedia

@Composable
fun BlogMedias(
    modifier: Modifier,
    mediaList: List<BlogMedia>,
) {
    val density = LocalDensity.current
    val context = LocalContext.current
    var containerWidth: Dp? by remember {
        mutableStateOf(null)
    }
//    LaunchedEffect(mediaList) {
//        mediaList
//            .filter { it.type == BlogMediaType.IMAGE || it.type == BlogMediaType.GIFV }
//            .take(6)
//            .forEach { media ->
//                ImageRequest.Builder(context)
//                    .data(media.url)
//                    .build()
//                    .let(context.imageLoader::enqueue)
//            }
//    }

    Box(
        modifier = modifier.onGloballyPositioned {
            containerWidth = it.size.width.pxToDp(density)
        }
    ) {

    }
}

@Composable
fun SingleImageMedia() {

}
