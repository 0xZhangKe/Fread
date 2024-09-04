package com.zhangke.fread.status.ui.label

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_bottom_label_edited_at
import org.jetbrains.compose.resources.stringResource

@Composable
fun StatusBottomEditedLabel(
    modifier: Modifier,
    editedAt: String,
    style: StatusStyle,
) {
    Text(
        modifier = modifier,
        text = stringResource(Res.string.status_ui_bottom_label_edited_at, editedAt),
        textAlign = TextAlign.Start,
        color = style.secondaryFontColor,
        style = style.bottomLabelStyle.textStyle,
    )
}
