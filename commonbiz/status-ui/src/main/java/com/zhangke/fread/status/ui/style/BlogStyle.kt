package com.zhangke.fread.status.ui.style

import androidx.compose.runtime.Composable

data class BlogStyle(
    val contentMaxLine: Int,
    val contentFontSizeSp: Float,
    val spoilerFontSizeSp: Float,
)

object BlogStyleDefaults {

    const val contentMaxLine: Int = 10

    const val contentFontSizeSp: Float = 14F

    const val spoilerFontSizeSp: Float = 14F
}

@Composable
fun defaultBlogStyle(
    contentMaxLine: Int = BlogStyleDefaults.contentMaxLine,
    contentFontSizeSp: Float = BlogStyleDefaults.contentFontSizeSp,
    spoilerFontSizeSp: Float = BlogStyleDefaults.spoilerFontSizeSp,
) = BlogStyle(
    contentMaxLine = contentMaxLine,
    contentFontSizeSp = contentFontSizeSp,
    spoilerFontSizeSp = spoilerFontSizeSp,
)
