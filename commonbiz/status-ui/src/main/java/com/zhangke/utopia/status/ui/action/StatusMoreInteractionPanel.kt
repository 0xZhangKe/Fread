package com.zhangke.utopia.status.ui.action

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
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
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.utopia.common.status.model.StatusUiInteraction

@Composable
fun StatusMoreInteractionIcon(
    modifier: Modifier,
    moreActionList: List<StatusUiInteraction>,
    onActionClick: (StatusUiInteraction) -> Unit,
) {
    var showMorePopup by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier) {
        if (moreActionList.isNotEmpty()) {
            SimpleIconButton(
                onClick = {
                    showMorePopup = !showMorePopup
                },
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
            )

            DropdownMenu(
                expanded = showMorePopup,
                onDismissRequest = { showMorePopup = false },
            ) {
                moreActionList.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(text = it.actionName)
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = it.logo,
                                contentDescription = it.actionName,
                            )
                        },
                        onClick = {
                            onActionClick(it)
                            showMorePopup = false
                        },
                    )
                }
            }
        }
    }
}
