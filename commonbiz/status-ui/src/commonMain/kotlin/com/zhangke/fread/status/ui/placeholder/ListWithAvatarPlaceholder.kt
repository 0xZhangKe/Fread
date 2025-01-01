package com.zhangke.fread.status.ui.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.freadPlaceholder

@Composable
fun TitleWithAvatarItemPlaceholder(
    modifier: Modifier,
) {
    Row(
        modifier = modifier.height(48.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(36.dp)
                .clip(RoundedCornerShape(6.dp))
                .freadPlaceholder(true),
        )
        Spacer(modifier = Modifier.width(16.dp))
        Box(
            modifier = Modifier.size(width = 180.dp, height = 18.dp)
                .clip(RoundedCornerShape(4.dp))
                .freadPlaceholder(true),
        )
    }
}
