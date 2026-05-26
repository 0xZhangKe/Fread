package com.zhangke.fread.commonbiz.shared.screen.publish.alt

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.zhangke.fread.localization.LocalizedString
import org.jetbrains.compose.resources.stringResource

@Composable
fun AltGeneratorButton(
    modifier: Modifier,
    state: AltGeneratorState,
    imageUri: String,
) {
    val generateState by state.generateState
    val available by state.available
    Box(modifier = modifier) {
        TextButton(
            onClick = { state.generate(imageUri) },
            enabled = generateState !is GenerateState.Generating && available,
        ) {
            Text(
                text = stringResource(
                    if (generateState is GenerateState.Generating) {
                        LocalizedString.alt_text_cancel_button
                    } else {
                        LocalizedString.alt_text_generate_button
                    }
                ),
            )
        }
    }
}
