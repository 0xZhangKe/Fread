package com.zhangke.fread.debug.screens.collapsable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TopBarWithTabLayout
import com.zhangke.framework.ktx.second
import com.zhangke.framework.utils.toPx

class CollapsableDemoScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        TopBarWithTabLayout(
            topBarContent = {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Title",
                    fontSize = 26.sp,
                    color = Color.Blue,
                )
            },
            tabContent = {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "Subtitle",
                    fontSize = 22.sp,
                    color = Color.Magenta,
                )
            },
        ) {
            val list = remember {
                List(100) { "Item $it" }
            }

            LazyColumn(
                modifier = Modifier
                    .background(Color.Yellow),
            ) {
                items(list) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = it,
                    )
                }
            }
        }
    }

}