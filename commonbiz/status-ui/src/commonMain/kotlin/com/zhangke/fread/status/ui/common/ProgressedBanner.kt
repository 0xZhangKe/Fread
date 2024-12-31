package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.img_banner_background
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProgressedBanner(
    modifier: Modifier,
    url: String?,
) {
    Box(modifier = modifier.height(180.dp)) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(Res.drawable.img_banner_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        val maskColor = MaterialTheme.colorScheme.inverseSurface
        AutoSizeImage(
            url.orEmpty(),
            modifier = Modifier
                .fillMaxSize()
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                maskColor.copy(alpha = 0.3F),
                                maskColor.copy(alpha = 0F),
                            ),
                        ),
                    )
                },
            contentScale = ContentScale.Crop,
            contentDescription = "banner",
        )
    }
}
