package com.zhangke.framework.composable

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchToolbar(
    onBackClick: () -> Unit,
    placeholderText: String,
    onQueryChange: (query: String) -> Unit,
    onSearch: (query: String) -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    var query by remember {
        mutableStateOf("")
    }
    SearchBar(
        query = query,
        onQueryChange = {
            query = it
            onQueryChange(it)
        },
        onSearch = onSearch,
        leadingIcon = {
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.ArrowBack),
                    "back"
                )
            }
        },
        trailingIcon = {
            IconButton(onClick = { query = "" }) {
                Icon(
                    painter = rememberVectorPainter(image = Icons.Filled.Clear),
                    contentDescription = "clear"
                )
            }
        },
        active = true,
        placeholder = {
            Text(text = placeholderText)
        },
        onActiveChange = {
            if (!it) onBackClick()
        },
        content = content,
    )
}