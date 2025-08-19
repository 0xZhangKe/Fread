package com.zhangke.fread.status.ui.common

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.fread.status.model.Relationships
import com.zhangke.fread.statusui.Res
import com.zhangke.fread.statusui.status_ui_relationship_btn_dialog_content_cancel_blocking
import com.zhangke.fread.statusui.status_ui_relationship_btn_dialog_content_cancel_follow
import com.zhangke.fread.statusui.status_ui_relationship_btn_dialog_content_cancel_follow_request
import com.zhangke.fread.statusui.status_ui_user_detail_relationship_blocking
import com.zhangke.fread.statusui.status_ui_user_detail_relationship_follow_back
import com.zhangke.fread.statusui.status_ui_user_detail_relationship_following
import com.zhangke.fread.statusui.status_ui_user_detail_relationship_mutuals
import com.zhangke.fread.statusui.status_ui_user_detail_relationship_not_follow
import com.zhangke.fread.statusui.status_ui_user_detail_relationship_requested
import org.jetbrains.compose.resources.stringResource

@Composable
fun RelationshipStateButton(
    modifier: Modifier,
    relationship: Relationships,
    onUnblockClick: () -> Unit,
    onFollowClick: () -> Unit,
    onUnfollowClick: () -> Unit,
    onCancelFollowRequestClick: () -> Unit,
) {
    var showUnfollowDialog by remember { mutableStateOf(false) }
    when {
        relationship.blocking -> {
            var showDialog by remember { mutableStateOf(false) }
            Button(
                modifier = modifier,
                onClick = { showDialog = true },
            ) {
                Text(text = stringResource(Res.string.status_ui_user_detail_relationship_blocking))
            }
            if (showDialog) {
                AlertConfirmDialog(
                    content = stringResource(Res.string.status_ui_relationship_btn_dialog_content_cancel_blocking),
                    onConfirm = onUnblockClick,
                    onDismissRequest = { showDialog = false }
                )
            }
        }

        relationship.requested == true -> {
            var showDialog by remember { mutableStateOf(false) }
            FilledTonalButton(
                modifier = modifier,
                onClick = { showDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            ) {
                Text(text = stringResource(Res.string.status_ui_user_detail_relationship_requested))
            }
            if (showDialog) {
                AlertConfirmDialog(
                    content = stringResource(Res.string.status_ui_relationship_btn_dialog_content_cancel_follow_request),
                    onConfirm = onCancelFollowRequestClick,
                    onDismissRequest = { showDialog = false }
                )
            }
        }

        relationship.following && relationship.followedBy -> {
            FilledTonalButton(
                modifier = modifier,
                onClick = { showUnfollowDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            ) {
                Text(text = stringResource(Res.string.status_ui_user_detail_relationship_mutuals))
            }
        }

        relationship.following -> {
            FilledTonalButton(
                modifier = modifier,
                onClick = { showUnfollowDialog = true },
            ) {
                Text(text = stringResource(Res.string.status_ui_user_detail_relationship_following))
            }
        }

        relationship.followedBy -> {
            Button(
                modifier = modifier,
                onClick = onFollowClick,
            ) {
                Text(text = stringResource(Res.string.status_ui_user_detail_relationship_follow_back))
            }
        }

        else -> {
            Button(
                modifier = modifier,
                onClick = onFollowClick,
            ) {
                Text(text = stringResource(Res.string.status_ui_user_detail_relationship_not_follow))
            }
        }
    }
    if (showUnfollowDialog) {
        AlertConfirmDialog(
            content = stringResource(Res.string.status_ui_relationship_btn_dialog_content_cancel_follow),
            onConfirm = onUnfollowClick,
            onDismissRequest = { showUnfollowDialog = false }
        )
    }
}
