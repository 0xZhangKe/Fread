package com.zhangke.fread.commonbiz.shared.screen.publish.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.PopupProperties
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.fread.commonbiz.shared.screen.publish.PublishSettingLabel
import com.zhangke.fread.commonbiz.shared.screen.publish.multi.describeStringId
import com.zhangke.fread.status.model.StatusVisibility
import org.jetbrains.compose.resources.stringResource

@Composable
fun PostStatusVisibilityUi(
    modifier: Modifier,
    visibility: StatusVisibility,
    changeable: Boolean,
    onVisibilitySelect: (StatusVisibility) -> Unit,
) {
    var showSelector by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier) {
        PublishSettingLabel(
            modifier = modifier.noRippleClick(enabled = changeable) { showSelector = true },
            label = stringResource(visibility.describeStringId),
            icon = Icons.Default.Public,
            tail = if (changeable) {
                {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                    )
                }
            } else {
                null
            },
        )
        DropdownMenu(
            expanded = showSelector,
            onDismissRequest = { showSelector = false },
            properties = PopupProperties(),
        ) {
            StatusVisibility.entries.forEach {
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
