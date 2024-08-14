package com.zhangke.fread.status.ui.common

import androidx.compose.runtime.Composable
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.status.ui.style.StatusStyle

@Composable
fun BlogTranslationState(
    translationUiState: BlogTranslationUiState,
    style: StatusStyle,
) {
    if (translationUiState is BlogTranslationUiState.Success) return

    if (translationUiState is BlogTranslationUiState.Success) {

    } else if (translationUiState is BlogTranslationUiState.Failure) {

    }
}
