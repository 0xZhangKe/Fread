package com.zhangke.fread.status.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.zhangke.framework.architect.theme.FreadTheme

@Preview
@Composable
fun StatusPlaceHolderPreview() {
    FreadTheme {
        StatusPlaceHolder(
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
