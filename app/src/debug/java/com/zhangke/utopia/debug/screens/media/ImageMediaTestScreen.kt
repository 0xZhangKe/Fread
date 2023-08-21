package com.zhangke.utopia.debug.screens.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.zhangke.framework.composable.Toolbar
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogMediaMeta
import com.zhangke.utopia.status.blog.BlogMediaType
import com.zhangke.utopia.status.blog.asImageMeta

class ImageMediaTestScreen : AndroidScreen() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current!!
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = "Debug",
                    onBackClick = navigator::pop,
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(
                    onClick = { navigator.push(SingleImageTestScreen()) }
                ) {
                    Text(text = "Single Image Media")
                }
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = { navigator.push(DoubleImageTestScreen()) }
                ) {
                    Text(text = "Double Image Media")
                }
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = { navigator.push(ThreeImageMediaTestScreen()) }
                ) {
                    Text(text = "Three Image Media")
                }
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = { navigator.push(QuadrupleImageMediaTestScreen()) }
                ) {
                    Text(text = "Four Image Media")
                }
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = { navigator.push(FiveImageMediaTestScreen()) }
                ) {
                    Text(text = "Five Image Media")
                }
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = { navigator.push(SixfoldImageTestScreen()) }
                ) {
                    Text(text = "Six Image Media")
                }
            }
        }
    }
}

@Composable
internal fun MockBlog(
    label: String?,
    mediaContent: @Composable () -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(
                modifier = Modifier
                    .height(10.dp)
                    .width(1.dp)
            )
            mediaContent()
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                color = Color.Gray,
            )
        }
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            color = Color.Red,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            text = label.orEmpty(),
        )
    }
}

internal fun mockBlogImageMedia(
    url: String,
    aspect: Float,
): BlogMedia {
    return BlogMedia(
        id = "mock_id",
        url = url,
        type = BlogMediaType.IMAGE,
        previewUrl = null,
        remoteUrl = null,
        description = "This is a mock image media!",
        blurhash = "",
        meta = BlogMediaMeta.ImageMeta(
            original = BlogMediaMeta.ImageMeta.LayoutMeta(
                width = null,
                height = null,
                size = null,
                aspect = aspect,
            ),
            small = null,
            focus = null,
        )
    )
}

internal fun BlogMedia.fetchDebugAspectRatio(): Float {
    return meta!!.asImageMeta().original!!.aspect!!
}
