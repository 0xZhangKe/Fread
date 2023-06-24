package com.zhangke.utopia.pages.feeds.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.Toolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedsDetailPage(
){
    Scaffold(
        topBar = {
            Toolbar(title = "")
        }
    ) {
        Box(modifier = Modifier.padding(it)){

        }
    }
}
