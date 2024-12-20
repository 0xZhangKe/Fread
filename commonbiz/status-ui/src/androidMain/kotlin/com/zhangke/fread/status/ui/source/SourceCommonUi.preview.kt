package com.zhangke.fread.status.ui.source

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.intercept.Interceptor
import com.seiko.imageloader.model.ImageResult
import com.zhangke.framework.architect.theme.FreadTheme
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher

@Preview
@Composable
fun PreviewSourceCommonUi() {
    FreadTheme {
        val context = LocalContext.current
        CompositionLocalProvider(
            LocalImageLoader provides testImageLoader,
            LocalActivityBrowserLauncher provides BrowserLauncher(context),
        ) {
            SourceCommonUi(
                thumbnail = "",
                title = "Title",
                subtitle = "Subtitle",
                description = "Description",
                protocolName = "Protocol",
                showDivider = false,
            )
        }
    }
}

@Preview
@Composable
fun PreviewSourceCommonUiWithDivider() {
    FreadTheme {
        val context = LocalContext.current
        CompositionLocalProvider(
            LocalImageLoader provides testImageLoader,
            LocalActivityBrowserLauncher provides BrowserLauncher(context),
        ) {
            SourceCommonUi(
                thumbnail = "",
                title = "Title",
                subtitle = "Subtitle",
                description = "Description",
                protocolName = "Protocol",
                showDivider = true,
            )
        }
    }
}

private val testImageLoader: ImageLoader
    get() = ImageLoader {
        interceptor {
            useDefaultInterceptors = false
            addInterceptor(Interceptor {
                ImageResult.OfPainter(ColorPainter(Color.White))
            })
        }
    }