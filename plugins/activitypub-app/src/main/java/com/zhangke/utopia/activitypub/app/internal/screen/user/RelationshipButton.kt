package com.zhangke.utopia.activitypub.app.internal.screen.user

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.zhangke.activitypub.entities.ActivityPubRelationshipEntity
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.StyledTextButton
import com.zhangke.framework.composable.TextButtonStyle
import com.zhangke.utopia.activitypub.app.R

@Composable
fun RelationshipStateButton(
    modifier: Modifier,
    relationship: RelationshipUiState,
    onUnblockClick: () -> Unit,
    onFollowClick: () -> Unit,
    onUnfollowClick: () -> Unit,
    onCancelFollowRequestClick: () -> Unit,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit,
) {
    when (relationship) {
        RelationshipUiState.BLOCKING -> {
            var showDialog by remember {
                mutableStateOf(false)
            }
            RelationshipTextButton(
                modifier = modifier,
                style = TextButtonStyle.ALERT,
                text = stringResource(R.string.activity_pub_user_detail_relationship_blocking),
                onClick = {
                    showDialog = true
                },
            )
            if (showDialog) {
                AlertConfirmDialog(
                    content = stringResource(R.string.activity_pub_relationship_btn_dialog_content_cancel_blocking),
                    onConfirm = onUnblockClick,
                    onDismissRequest = { showDialog = false }
                )
            }
        }

        RelationshipUiState.BLOCKED_BY -> {
            RelationshipTextButton(
                modifier = modifier,
                style = TextButtonStyle.DISABLE,
                text = stringResource(R.string.activity_pub_user_detail_relationship_not_follow),
                onClick = onUnfollowClick,
            )
        }

        RelationshipUiState.FOLLOWING -> {
            var showDialog by remember {
                mutableStateOf(false)
            }
            RelationshipTextButton(
                modifier = modifier,
                style = TextButtonStyle.STANDARD,
                text = stringResource(R.string.activity_pub_user_detail_relationship_following),
                onClick = {
                    showDialog = true
                },
            )
            if (showDialog) {
                AlertConfirmDialog(
                    content = stringResource(R.string.activity_pub_relationship_btn_dialog_content_cancel_follow),
                    onConfirm = onUnfollowClick,
                    onDismissRequest = { showDialog = false }
                )
            }
        }

        RelationshipUiState.FOLLOWED_BY, RelationshipUiState.CAN_FOLLOW -> {
            RelationshipTextButton(
                modifier = modifier,
                style = TextButtonStyle.ACTIVE,
                text = stringResource(R.string.activity_pub_user_detail_relationship_not_follow),
                onClick = onFollowClick,
            )
        }

        RelationshipUiState.REQUESTED -> {
            var showDialog by remember {
                mutableStateOf(false)
            }
            RelationshipTextButton(
                modifier = modifier,
                style = TextButtonStyle.STANDARD,
                text = stringResource(R.string.activity_pub_user_detail_relationship_requested),
                onClick = { showDialog = true },
            )
            if (showDialog) {
                AlertConfirmDialog(
                    content = stringResource(R.string.activity_pub_relationship_btn_dialog_content_cancel_follow_request),
                    onConfirm = onCancelFollowRequestClick,
                    onDismissRequest = { showDialog = false }
                )
            }
        }

        RelationshipUiState.REQUEST_BY -> {
            FollowRequestBy(
                modifier = modifier,
                onAcceptClick = onAcceptClick,
                onRejectClick = onRejectClick,
            )
        }

        RelationshipUiState.UNKNOWN -> {
            Box(modifier = modifier)
        }
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
            text = stringResource(R.string.activity_pub_user_detail_request_by_tip),
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
