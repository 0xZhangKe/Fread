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
import com.zhangke.fread.common.translate.PostTranslationState
import com.zhangke.fread.common.translate.PostTranslationStatus
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.ui.style.StatusStyle
import org.jetbrains.compose.resources.stringResource

@Composable
fun BlogTranslateLabel(
    modifier: Modifier,
    style: StatusStyle,
    postTranslationState: PostTranslationState,
    onShowOriginalClick: () -> Unit,
) {
    if (!postTranslationState.showTranslation) return
    Row(
        modifier = modifier.padding(vertical = style.contentStyle.contentVerticalSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HorizontalDivider(modifier = Modifier.weight(1F))
        if (postTranslationState.status is PostTranslationStatus.Translated) {
            Text(
                modifier = Modifier
                    .clickable { onShowOriginalClick() }
                    .padding(horizontal = 16.dp),
                text = stringResource(LocalizedString.statusUiTranslateShowOriginal),
                style = style.infoLineStyle.descStyle,
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp),
                text = stringResource(LocalizedString.statusUiTranslating),
                style = style.infoLineStyle.descStyle,
            )
        }
        HorizontalDivider(
            modifier = Modifier.weight(1F)
        )
    }
}
