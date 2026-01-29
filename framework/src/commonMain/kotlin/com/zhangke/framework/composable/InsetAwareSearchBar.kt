package com.zhangke.framework.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarColors
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsetAwareSearchBar(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    inputField: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.statusBars,
    insetContainerColor: Color = MaterialTheme.colorScheme.surface,
    colors: SearchBarColors = SearchBarDefaults.colors(),
    tonalElevation: Dp = SearchBarDefaults.TonalElevation,
    shadowElevation: Dp = SearchBarDefaults.ShadowElevation,
    shape: Shape = SearchBarDefaults.inputFieldShape,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Box(
        modifier = Modifier
            .windowInsetsPadding(windowInsets)
            .background(insetContainerColor)
            .then(modifier),
    ) {
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            windowInsets = WindowInsets(0, 0, 0, 0),
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            inputField = inputField,
            colors = colors,
            tonalElevation = tonalElevation,
            shadowElevation = shadowElevation,
            shape = shape,
            content = content,
        )
    }
}
