package com.zhangke.fread.status.ui.publish

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.TwoTextsInRow
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.ui.richtext.FreadRichText
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun NameAndAccountInfo(
    modifier: Modifier,
    humanizedName: RichText,
    handle: String,
    style: StatusStyle,
) {
    TwoTextsInRow(
        firstText = {
            FreadRichText(
                modifier = Modifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                richText = humanizedName,
                onUrlClick = {},
                fontWeight = FontWeight.SemiBold,
                fontSizeSp = style.infoLineStyle.nameSize.value,
            )
        },
        secondText = {
            Text(
                modifier = Modifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                text = handle,
                color = style.secondaryFontColor,
                style = style.infoLineStyle.descStyle,
            )
        },
        spacing = 2.dp,
        modifier = modifier,
    )
}
