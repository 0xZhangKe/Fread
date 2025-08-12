package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.StyledTextButton
import com.zhangke.framework.composable.TextButtonStyle
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
import com.zhangke.fread.statusui.status_ui_user_detail_request_by_tip
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
            OutlinedButton(
                modifier = modifier,
                onClick = { showDialog = true },
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
            OutlinedButton(
                modifier = modifier,
                onClick = { showUnfollowDialog = true },
            ) {
                Text(text = stringResource(Res.string.status_ui_user_detail_relationship_mutuals))
            }
        }

        relationship.following -> {
            OutlinedButton(
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

@Composable
private fun RelationshipTextButton(
    modifier: Modifier,
    text: String,
    style: TextButtonStyle,
    onClick: () -> Unit,
) {
    StyledTextButton(
        modifier = modifier,
        text = text,
        style = style,
        onClick = onClick,
    )
}

@Composable
private fun FollowRequestBy(
    modifier: Modifier,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.Red,
                shape = RoundedCornerShape(6.dp),
            )
            .padding(horizontal = 6.dp, vertical = 6.dp),
    ) {
        Text(
            text = stringResource(Res.string.status_ui_user_detail_request_by_tip),
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
        )
        Row(
            modifier = Modifier.padding(top = 4.dp),
        ) {
            SimpleIconButton(
                modifier = Modifier
                    .size(32.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                onClick = onRejectClick,
                imageVector = Icons.Default.Clear,
                contentDescription = "Reject",
            )

            SimpleIconButton(
                modifier = Modifier
                    .size(32.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                ),
                onClick = onAcceptClick,
                imageVector = Icons.Default.Check,
                contentDescription = "Accept",
            )
        }
    }
}

enum class RelationshipUiState {
    BLOCKING,
    BLOCKED_BY,
    FOLLOWING,
    FOLLOWED_BY,
    REQUESTED,
    REQUEST_BY,
    CAN_FOLLOW,
    UNKNOWN,
}
