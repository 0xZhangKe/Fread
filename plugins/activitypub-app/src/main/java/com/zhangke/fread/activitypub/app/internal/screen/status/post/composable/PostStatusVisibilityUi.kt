package com.zhangke.fread.activitypub.app.internal.screen.status.post.composable

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.zhangke.fread.activitypub.app.internal.model.PostStatusVisibility

@Composable
internal fun PostStatusVisibilityUi(
    modifier: Modifier,
    visibility: PostStatusVisibility,
    onVisibilitySelect: (PostStatusVisibility) -> Unit,
) {
    var showSelector by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(6.dp),
                )
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .clickable { showSelector = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                imageVector = Icons.Default.Public,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
            Box(modifier = Modifier.width(4.dp))
            Text(
                text = stringResource(visibility.describeStringId),
                style = MaterialTheme.typography.bodySmall,
            )
            Box(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = showSelector,
            onDismissRequest = { showSelector = false },
            properties = PopupProperties(),
        ) {
            PostStatusVisibility.entries.forEach {
                DropdownMenuItem(
                    text = {
                        Text(text = stringResource(it.describeStringId))
                    },
                    onClick = {
                        showSelector = false
                        onVisibilitySelect(it)
                    },
                )
            }
        }
    }
}
