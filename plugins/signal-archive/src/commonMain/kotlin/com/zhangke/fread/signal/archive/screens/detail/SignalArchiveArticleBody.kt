package com.zhangke.fread.signal.archive.screens.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.fread.signal.archive.model.SignalArchiveArticle

@Composable
internal expect fun SignalArchiveArticleBody(
    html: String,
    modifier: Modifier,
    onLinkClick: (String) -> Unit,
)

internal fun signalArchiveArticleDocument(
    article: SignalArchiveArticle,
    foreground: Int,
    background: Int,
    linkColor: Int,
): String {
    fun Int.cssColor() = "#" + (this and 0xFFFFFF).toString(16).padStart(6, '0')
    val metadata = listOfNotNull(article.sourceTitle, article.publishedAt).joinToString(" · ")
    return """
        <!doctype html><html><head>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <meta charset="UTF-8">
        <style>
        body { margin: 0; padding: 16px; color: ${foreground.cssColor()}; background: ${background.cssColor()}; font-family: sans-serif; font-size: 16px; line-height: 1.6; overflow-wrap: anywhere; }
        h1 { font-size: 1.5em; line-height: 1.3; }
        .metadata { opacity: 0.7; font-size: 0.85em; margin-bottom: 24px; }
        a { color: ${linkColor.cssColor()}; }
        img, video { max-width: 100%; height: auto; }
        pre { overflow-x: auto; white-space: pre; padding: 12px; }
        table { display: block; overflow-x: auto; border-collapse: collapse; max-width: 100%; }
        td, th { padding: 8px; border: 1px solid currentColor; }
        blockquote { margin-left: 0; padding-left: 16px; border-left: 3px solid currentColor; }
        </style></head><body>
        <h1>${escapeArticleText(article.title)}</h1>
        <div class="metadata">${escapeArticleText(metadata)}</div>
        ${article.content}
        </body></html>
    """.trimIndent()
}

private fun escapeArticleText(text: String): String = text
    .replace("&", "&amp;")
    .replace("<", "&lt;")
    .replace(">", "&gt;")
    .replace("\"", "&quot;")
    .replace("'", "&#39;")
