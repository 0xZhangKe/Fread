package com.zhangke.fread.status.ui.richtext

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.richtext.RichText

@Composable
fun RichTextWithIcon(
    modifier: Modifier = Modifier,
    text: RichText,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    startIcon: (@Composable () -> Unit)? = null,
    endIcon: (@Composable () -> Unit)? = null,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onMentionClick: (Mention) -> Unit = {},
    onMentionDidClick: (String) -> Unit = {},
    onHashtagClick: (HashtagInStatus) -> Unit = {},
    onMaybeHashtagClick: (String) -> Unit = {},
    onUrlClick: (url: String) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        startIcon?.invoke()
        FreadRichText(
            modifier = Modifier,
            richText = text,
            color = color,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            lineHeight = lineHeight,
            textAlign = textAlign,
            onMentionClick = onMentionClick,
            onMentionDidClick = onMentionDidClick,
            onHashtagClick = onHashtagClick,
            onMaybeHashtagClick = onMaybeHashtagClick,
            onUrlClick = onUrlClick,
            overflow = overflow,
            maxLines = maxLines,
            fontSize = fontSize,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            softWrap = softWrap,
            onTextLayout = onTextLayout,
            style = style,
        )
        endIcon?.invoke()
    }
}
