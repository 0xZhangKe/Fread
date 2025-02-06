package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.ui.style.StatusStyle
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_translate_show_original
import com.zhangke.fread.statusui.status_ui_translating
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlogTranslateLabel(
    modifier: Modifier,
    style: StatusStyle,
    blogTranslationState: BlogTranslationUiState?,
    onShowOriginalClick: () -> Unit,
) {
    if (blogTranslationState == null || !blogTranslationState.support) return
    if (!blogTranslationState.translating && !blogTranslationState.showingTranslation) return
    Row(
        modifier = modifier
            .padding(vertical = style.contentStyle.contentVerticalSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1F)
        )
        if (blogTranslationState.showingTranslation) {
            Text(
                modifier = Modifier
                    .clickable {
                        onShowOriginalClick()
                    }
                    .padding(horizontal = 16.dp),
                text = stringResource(Res.string.status_ui_translate_show_original),
                style = style.infoLineStyle.descStyle,
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = stringResource(Res.string.status_ui_translating),
                style = style.infoLineStyle.descStyle,
            )
        }
        HorizontalDivider(
            modifier = Modifier.weight(1F)
        )
    }
}
