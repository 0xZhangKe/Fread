package com.zhangke.framework.composable.pick

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.zhangke.framework.utils.PlatformUri

@Composable
actual fun PickVisualMediaLauncherContainer(
    onResult: (List<PlatformUri>) -> Unit,
    maxItems: Int,
    content: @Composable PickVisualMediaLauncherContainerScope.() -> Unit,
) {
    val scope = remember {
        PickVisualMediaLauncherContainerScope()
    }
    with(scope) {
        content()
    }
}

actual class PickVisualMediaLauncherContainerScope {
    actual fun launchImage() {
        TODO("Not yet implemented")
    }

    actual fun launchMedia() {
        TODO("Not yet implemented")
    }
}
