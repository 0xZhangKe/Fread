package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun BlogTranslationState(
    modifier: Modifier,
    translationUiState: BlogTranslationUiState,
    style: StatusStyle,
) {
    if (translationUiState is BlogTranslationUiState.Success) return
    var parentWidth: Dp by remember {
        mutableStateOf(300.dp)
    }
    Row(
        modifier = modifier
            .padding(vertical = style.contentStyle.contentVerticalSpacing)
            .onGloballyPositioned {
                parentWidth = it.size.width.dp
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (translationUiState is BlogTranslationUiState.Loading) {

        } else {

        }
        HorizontalDivider(
            modifier = Modifier.weight(1F)
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .widthIn(max = parentWidth - 88.dp),
            text = "TranslationTranslationTranslationTranslationTranslationTranslation...",
        )
        HorizontalDivider(
            modifier = Modifier.weight(1F)
        )
    }
}
