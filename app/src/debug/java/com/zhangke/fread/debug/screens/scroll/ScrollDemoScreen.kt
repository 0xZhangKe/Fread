package com.zhangke.fread.debug.screens.scroll

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.commonbiz.illustration_celebrate
import org.jetbrains.compose.resources.painterResource

class ScrollDemoScreen : Screen {

    @Composable
    override fun Content() {
        val list = remember {
            List(100) {
                "Item $it"
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(list) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    var expand by remember {
                        mutableStateOf(false)
                    }
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                expand = !expand
                            },
                        text = if (expand) "$it \n $it \n $it" else it,
                    )
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        painter = painterResource(com.zhangke.fread.commonbiz.Res.drawable.illustration_celebrate),
                        contentDescription = "",
                    )
                }
            }
        }
    }
}
