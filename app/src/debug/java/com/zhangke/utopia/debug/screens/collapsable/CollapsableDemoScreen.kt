package com.zhangke.utopia.debug.screens.collapsable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen

class CollapsableDemoScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val scrollBehavior =
            TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
        Scaffold(
            topBar = {
                LargeTopAppBar(
                    scrollBehavior = scrollBehavior,
                    title = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(Color.Black),
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = "Title",
                            )
                        }
                    },
                )
            },
        ) { paddings ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddings)
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(150.dp)
//                        .background(Color.Black),
//                ) {
//                    Text(
//                        modifier = Modifier.align(Alignment.Center),
//                        text = "Title",
//                    )
//                }

            val list = remember {
                List(100) { "Item $it" }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
//                        .weight(1F),
            ) {
                items(list) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it,
                    )
                }
            }
        }
//        }
    }

    class TowLineNestedScrollConnection : NestedScrollConnection {

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            return super.onPreScroll(available, source)
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            return super.onPreFling(available)
        }
    }
}