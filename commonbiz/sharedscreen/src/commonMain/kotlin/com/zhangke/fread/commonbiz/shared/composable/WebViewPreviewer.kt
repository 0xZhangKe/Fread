package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun WebViewPreviewer(
    html: String,
    modifier: Modifier = Modifier,
)

internal fun warpBlogContentHtml(
    html: String,
    fontColor: Int,
): String {
    // val colorString = String.format("#%06X", 0xFFFFFF and fontColor)
    val colorString = "#${fontColor.toString(16)}"
    return """
        <html>
        <head>
        <style>
        body {
            color: ${colorString};
        }
        </style>
        </head>
        <body>
        $html
        </body>
        </html>
    """.trimIndent()
}