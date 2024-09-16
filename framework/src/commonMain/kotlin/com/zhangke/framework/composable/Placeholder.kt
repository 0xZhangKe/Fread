package com.zhangke.framework.composable

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import com.eygraber.compose.placeholder.PlaceholderHighlight
import com.eygraber.compose.placeholder.material3.fade
import com.eygraber.compose.placeholder.material3.placeholder

@Composable
fun Modifier.freadPlaceholder(
    visible: Boolean,
    color: Color = Color.Unspecified,
    shape: Shape = RectangleShape,
    // highlight: PlaceholderHighlight? = null,
    placeholderFadeAnimationSpec: AnimationSpec<Float> = spring(),
    contentFadeAnimationSpec: AnimationSpec<Float> = spring(),
): Modifier = then(
    Modifier.placeholder(
        visible = visible,
        color = color,
        shape = shape,
        highlight = PlaceholderHighlight.fade(),
        placeholderFadeAnimationSpec = placeholderFadeAnimationSpec,
        contentFadeAnimationSpec = contentFadeAnimationSpec,
    )
)
