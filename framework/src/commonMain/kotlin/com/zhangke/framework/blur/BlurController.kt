package com.zhangke.framework.blur

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState

@Stable
class BlurController private constructor(
    initEnabled: Boolean = true,
) {

    var enabled by mutableStateOf(initEnabled)

    var hazeState: HazeState? by mutableStateOf(null)

    companion object {

        fun create(): BlurController {
            return BlurController(true)
        }
    }
}

@Composable
fun rememberBlurController(): BlurController {
    val controller = remember { BlurController.create() }
    val enableBlurAppBarStyle = LocalEnableBlurAppBarStyle.current
    LaunchedEffect(controller, enableBlurAppBarStyle) {
        controller.enabled = enableBlurAppBarStyle
    }
    return controller
}

val LocalBlurController = compositionLocalOf<BlurController?> {
    null
}

@Composable
fun Modifier.applyBlurSource(enabled: Boolean = true): Modifier {
    if (!enabled) return this
    val controller = LocalBlurController.current
    if (controller == null || !controller.enabled) return this
    val state = rememberHazeState(blurEnabled = true)
    DisposableEffect(enabled, controller.hazeState) {
        controller.hazeState = state
        onDispose {
            if (controller.hazeState == state) {
                controller.hazeState = null
            }
        }
    }
    return this.then(Modifier.hazeSource(state))
}

@Composable
fun Modifier.applyBlurEffect(
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
): Modifier {
    if (!enabled) return this
    val controller = LocalBlurController.current
    if (controller == null || !controller.enabled) return this
    val state = controller.hazeState ?: return this
    return this.hazeEffect(
        state = state,
        style = HazeMaterials.thick(containerColor)
    )
}

@Composable
fun blurEffectContainerColor(
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
): Color {
    val enableContainerColor = enableContainerColor(enabled)
    return if (enableContainerColor) containerColor else Color.Transparent
}

@Composable
private fun enableContainerColor(enabled: Boolean = true): Boolean {
    if (!enabled) return true
    val controller = LocalBlurController.current
    if (controller == null || !controller.enabled) return true
    controller.hazeState ?: return true
    return false
}

@Composable
private fun getBlurEffectContainerColor(
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.surface,
): Color {
    if (!enabled) return containerColor
    val controller = LocalBlurController.current
    if (controller == null || !controller.enabled) return containerColor
    controller.hazeState ?: return containerColor
    return Color.Transparent
}
