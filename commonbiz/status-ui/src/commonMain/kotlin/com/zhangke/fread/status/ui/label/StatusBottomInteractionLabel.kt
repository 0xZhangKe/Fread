package com.zhangke.fread.status.ui.label

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.sp
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.utils.formatToHumanReadable
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_interaction_label_boosted_count
import com.zhangke.fread.statusui.status_ui_interaction_label_favourited_count
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatusBottomInteractionLabel(
    modifier: Modifier,
    boostedCount: Int,
    favouritedCount: Int,
    style: StatusStyle,
    onBoostedClick: () -> Unit,
    onFavouritedClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        val boostedText = buildHighlightLabelText(
            highlight = boostedCount.formatToHumanReadable(),
            wholeText = stringResource(
                Res.string.status_ui_interaction_label_boosted_count,
                boostedCount.formatToHumanReadable(),
            ),
            style = style.bottomLabelStyle.textStyle,
            highLightColor = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            modifier = Modifier.noRippleClick { onBoostedClick() },
            text = boostedText,
            color = style.secondaryFontColor,
            style = style.bottomLabelStyle.textStyle,
        )

        val favouritedText = buildHighlightLabelText(
            highlight = favouritedCount.formatToHumanReadable(),
            wholeText = stringResource(
                Res.string.status_ui_interaction_label_favourited_count,
                favouritedCount.formatToHumanReadable(),
            ),
            style = style.bottomLabelStyle.textStyle,
            highLightColor = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            modifier = Modifier
                .padding(start = 6.dp)
                .noRippleClick { onFavouritedClick() },
            text = favouritedText,
            color = style.secondaryFontColor,
            style = style.bottomLabelStyle.textStyle,
        )
    }
}

private fun buildHighlightLabelText(
    highlight: String,
    wholeText: String,
    style: TextStyle,
    highLightColor: Color,
): AnnotatedString {
    return buildAnnotatedString {
        append(wholeText)
        val startIndex = wholeText.indexOf(highlight)
        val endIndex = startIndex + highlight.length
        if (startIndex in 0..<endIndex && endIndex <= wholeText.length) {
            val highlightFontSize = if (style.fontSize.isSpecified) {
                style.fontSize * 1.2
            } else {
                16.sp
            }
            addStyle(
                style = SpanStyle(
                    fontSize = highlightFontSize,
                    fontWeight = FontWeight.Medium,
                    color = highLightColor,
                ),
                start = startIndex,
                end = endIndex,
            )
        }
    }
}
