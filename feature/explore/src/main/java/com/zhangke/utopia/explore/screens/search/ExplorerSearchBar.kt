package com.zhangke.utopia.explore.screens.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen.ExplorerSearchBar() {
    var query by remember {
        mutableStateOf("")
    }
    var active by remember {
        mutableStateOf(false)
    }
    SearchBar(
        modifier = Modifier
            .searchPadding(active)
            .fillMaxWidth()
            .onFocusChanged {
                if (it.hasFocus && !active) {
                    active = true
                }
            },
        query = query,
        onQueryChange = {
            query = it
        },
        onSearch = {

        },
        active = active,
        onActiveChange = {

        },
    ) {
        BackHandler(active) {
            active = false
        }
        SearchContent(query)
    }
}

@Composable
private fun Screen.SearchContent(
    query: String,
) {
    val viewModel = getViewModel<SearchContentViewModel>()
    LaunchedEffect(query) {
        viewModel.onSearchQueryChanged(query)
    }

}

private fun Modifier.searchPadding(active: Boolean): Modifier {
    return if (active) {
        this
    } else {
        Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp) then this
    }
}
