package com.zhangke.fread.status.ui.bar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_edit_content_delete_dialog_content
import com.zhangke.fread.statusui.status_ui_edit_content_name_hint
import com.zhangke.fread.statusui.status_ui_edit_content_name_label
import com.zhangke.fread.statusui.status_ui_edit_content_name_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditContentTopBar(
    contentName: String,
    onBackClick: () -> Unit,
    onNameEdit: (String) -> Unit,
    onDeleteClick: () -> Unit,
) {

    var showDeleteConfirmDialog by remember {
        mutableStateOf(false)
    }
    var showEditNameDialog by remember {
        mutableStateOf(false)
    }
    Toolbar(
        title = contentName,
        onBackClick = onBackClick,
        actions = {
            SimpleIconButton(
                onClick = {
                    showEditNameDialog = true
                },
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit content name",
            )
            SimpleIconButton(
                onClick = {
                    showDeleteConfirmDialog = true
                },
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete content",
            )
        }
    )
    if (showDeleteConfirmDialog) {
        FreadDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            contentText = stringResource(Res.string.status_ui_edit_content_delete_dialog_content),
            onNegativeClick = {
                showDeleteConfirmDialog = false
            },
            onPositiveClick = {
                showDeleteConfirmDialog = false
                onDeleteClick()
            },
        )
    }
    if (showEditNameDialog) {
        EditContentNameDialog(
            name = contentName,
            onConfirmClick = onNameEdit,
            onDismissRequest = { showEditNameDialog = false },
        )
    }
}

@Composable
private fun EditContentNameDialog(
    name: String,
    onConfirmClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var inputtingNote by remember { mutableStateOf(name) }
    FreadDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        title = stringResource(Res.string.status_ui_edit_content_name_title),
        content = {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
                value = inputtingNote,
                onValueChange = {
                    inputtingNote = it
                },
                label = {
                    Text(
                        text = stringResource(Res.string.status_ui_edit_content_name_label)
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(Res.string.status_ui_edit_content_name_hint)
                    )
                },
            )
        },
        onNegativeClick = {
            onDismissRequest()
        },
        onPositiveClick = {
            if (inputtingNote.isNotEmpty()) {
                onDismissRequest()
                onConfirmClick(inputtingNote)
            }
        },
    )
}
