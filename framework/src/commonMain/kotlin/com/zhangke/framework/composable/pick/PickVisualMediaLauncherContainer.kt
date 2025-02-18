package com.zhangke.framework.composable.pick

import androidx.compose.runtime.Composable
import com.zhangke.framework.utils.PlatformUri

@Composable
expect fun PickVisualMediaLauncherContainer(
    onResult: (List<PlatformUri>) -> Unit,
    maxItems: Int = 1,
    content: @Composable PickVisualMediaLauncherContainerScope.() -> Unit,
)

expect class PickVisualMediaLauncherContainerScope {
    fun launchImage()

    fun launchMedia()
}
