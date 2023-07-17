package com.zhangke.utopia.activitypubapp.screen.server.trending.tags

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.BezierCurve
import com.zhangke.framework.composable.BezierCurveStyle
import com.zhangke.framework.composable.textString
import com.zhangke.framework.utils.toPx
import com.zhangke.utopia.activitypubapp.model.ActivityPubTag

@Composable
internal fun Screen.ServerTrendsTagsPage(
    host: String,
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val viewModel = getViewModel<ServerTrendsTagsViewModel>()
    viewModel.host = host
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onPageResume()
    }
    ServerTrendsTagsContent(
        uiState = uiState,
        contentCanScrollBackward = contentCanScrollBackward,
    )
}

@Composable
private fun ServerTrendsTagsContent(
    uiState: ServerTrendsTagsUiState,
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val listState = rememberLazyListState()
    val canScrollBackward by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex != 0 || listState.firstVisibleItemScrollOffset != 0
        }
    }
    contentCanScrollBackward.value = canScrollBackward
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
    ) {
        items(uiState.list) { item ->
            ServerTrendsTagsItem(item)
        }
    }
}

@Composable
private fun ServerTrendsTagsItem(tag: ActivityPubTag) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1F)
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp)
        ) {
            Text(
                text = tag.name,
                fontSize = 16.sp,
            )
            Text(
                modifier = Modifier.padding(top = 3.dp),
                text = textString(tag.description),
                fontSize = 12.sp,
            )
        }

        BezierCurve(
            modifier = Modifier
                .padding(end = 15.dp)
                .size(width = 70.dp, height = 40.dp),
            points = tag.history.reversed(),
            style = BezierCurveStyle.StrokeAndFill(
                fillBrush = SolidColor(Color.Blue),
                strokeBrush = SolidColor(Color.White),
                stroke = Stroke(width = 1.dp.toPx()),
            )
        )
    }
}
