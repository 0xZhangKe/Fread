package com.zhangke.framework.composable

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun PopupFloatingActionButton(
    modifier: Modifier = Modifier,
    popupContent: @Composable ColumnScope.() -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Log.d("U_TEST", "expanded: $expanded")
    FloatingActionButton(
        modifier = modifier,
        onClick = { if (!expanded) expanded = true },
    ) {
        val rotate by animateFloatAsState(if (expanded) 45f else 0f, label = "PopupFloatingActionButton")
        Icon(
            modifier = Modifier.rotate(rotate),
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
        )
    }
    DropdownMenu(
        modifier = Modifier.size(200.dp),
        expanded = expanded, onDismissRequest = { expanded = false }) {
        Box(modifier = Modifier.size(200.dp))
    }
//    if (expanded) {
//        Popup(
//            onDismissRequest = {
//                expanded = false
//            },
//        ) {
//            Column(modifier = Modifier.size(200.dp)) {
//                popupContent()
//            }
//        }
//    }
}
