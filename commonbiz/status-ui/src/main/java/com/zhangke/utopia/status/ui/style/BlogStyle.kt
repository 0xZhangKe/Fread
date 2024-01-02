package com.zhangke.utopia.status.ui.style

import androidx.compose.runtime.Composable

data class BlogStyle(
    val contentFontSizeSp: Float,
    val spoilerFontSizeSp: Float,
)

object BlogStyleDefaults {

    const val contentFontSizeSp: Float = 14F

    const val spoilerFontSizeSp: Float = 14F
}

@Composable
fun defaultBlogStyle() = BlogStyle(
    contentFontSizeSp = BlogStyleDefaults.contentFontSizeSp,
    spoilerFontSizeSp = BlogStyleDefaults.spoilerFontSizeSp,
)