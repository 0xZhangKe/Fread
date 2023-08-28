package com.zhangke.utopia.explore.pages.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun ExploreHomePage() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {



        val list = remember {
            mutableListOf<String>().apply {
                repeat(100) {
                    add("--------$it--------")
                }
            }
        }
        val state = rememberLazyListState()
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = state,
        ) {
            items(list) { item ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                ) {
                    Card {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(10.dp),
                            text = item,
                        )
                    }
                }
            }
        }
    }
}
