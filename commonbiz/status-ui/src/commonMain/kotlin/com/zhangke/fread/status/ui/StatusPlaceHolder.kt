package com.zhangke.fread.status.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.fread.status.ui.style.StatusInfoStyleDefaults

@Composable
fun StatusPlaceHolder(
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Spacer(Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .padding(top = 8.dp)
                .clip(CircleShape)
                .size(StatusInfoStyleDefaults.avatarSize)
                .freadPlaceholder(true),
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(100.dp, 16.dp)
                    .freadPlaceholder(true),
            )
            Spacer(Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .size(200.dp, 12.dp)
                    .freadPlaceholder(true),
            )
            Spacer(Modifier.height(4.dp))
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .height(14.dp)
                        .freadPlaceholder(true),
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.width(16.dp))
    }
}

@Composable
fun StatusListPlaceholder(
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            repeat(8) {
                StatusPlaceHolder(modifier = Modifier.fillMaxWidth())
                Box(modifier = Modifier.height(6.dp))
            }
        }
    }
}
