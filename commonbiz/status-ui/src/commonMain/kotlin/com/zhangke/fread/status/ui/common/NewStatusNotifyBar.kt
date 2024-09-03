package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_new_status
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewStatusNotifyBar(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier
            .height(40.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        onClick = onClick,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Scroll Up",
            )
            val tip = stringResource(Res.string.status_ui_new_status)
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = tip,
            )
        }
    }
}
