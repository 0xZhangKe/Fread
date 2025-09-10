package com.zhangke.fread.status.ui.common

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.Relationships
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
                Text(text = stringResource(LocalizedString.statusUiUserDetailRelationshipBlocking))
            }
            if (showDialog) {
                AlertConfirmDialog(
                    content = stringResource(LocalizedString.statusUiRelationshipBtnDialogContentCancelBlocking),
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
                Text(text = stringResource(LocalizedString.statusUiUserDetailRelationshipRequested))
            }
            if (showDialog) {
                AlertConfirmDialog(
                    content = stringResource(LocalizedString.statusUiRelationshipBtnDialogContentCancelFollowRequest),
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
                Text(text = stringResource(LocalizedString.statusUiUserDetailRelationshipMutuals))
            }
        }

        relationship.following -> {
            FilledTonalButton(
                modifier = modifier,
                onClick = { showUnfollowDialog = true },
            ) {
                Text(text = stringResource(LocalizedString.statusUiUserDetailRelationshipFollowing))
            }
        }

        relationship.followedBy -> {
            Button(
                modifier = modifier,
                onClick = onFollowClick,
            ) {
                Text(text = stringResource(LocalizedString.statusUiUserDetailRelationshipFollowBack))
            }
        }

        else -> {
            Button(
                modifier = modifier,
                onClick = onFollowClick,
            ) {
                Text(text = stringResource(LocalizedString.statusUiUserDetailRelationshipNotFollow))
            }
        }
    }
    if (showUnfollowDialog) {
        AlertConfirmDialog(
            content = stringResource(LocalizedString.statusUiRelationshipBtnDialogContentCancelFollow),
            onConfirm = onUnfollowClick,
            onDismissRequest = { showUnfollowDialog = false }
        )
    }
}
