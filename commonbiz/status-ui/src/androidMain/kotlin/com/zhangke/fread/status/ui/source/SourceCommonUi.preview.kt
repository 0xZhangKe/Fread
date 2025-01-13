package com.zhangke.fread.status.ui.source

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import com.seiko.imageloader.ImageLoader
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.intercept.Interceptor
import com.seiko.imageloader.model.ImageResult
import com.zhangke.framework.architect.theme.FreadTheme
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.browser.ActivityBrowserLauncher
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.status.model.IdentityRole

@Preview
@Composable
fun PreviewSourceCommonUi() {
    FreadTheme {
        CompositionLocalProvider(
            LocalImageLoader provides testImageLoader,
            LocalActivityBrowserLauncher provides testAndroidBrowserLauncher,
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
        CompositionLocalProvider(
            LocalImageLoader provides testImageLoader,
            LocalActivityBrowserLauncher provides testAndroidBrowserLauncher,
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

private val testAndroidBrowserLauncher: ActivityBrowserLauncher
    get() = object : ActivityBrowserLauncher {
        override fun launchWebTabInApp(
            uri: PlatformUri,
            role: IdentityRole?,
            checkAppSupportPage: Boolean,
        ) = Unit

        override fun launchBySystemBrowser(uri: PlatformUri) = Unit
    }