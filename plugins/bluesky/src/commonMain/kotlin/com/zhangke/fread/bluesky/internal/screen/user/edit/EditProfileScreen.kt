package com.zhangke.fread.bluesky.internal.screen.user.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.seiko.imageloader.ui.AutoSizeImage
import com.zhangke.framework.composable.AlertConfirmDialog
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.pick.PickVisualMediaLauncherContainer
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

@Serializable
data class EditProfileScreenNavKey(
    val locator: PlatformLocator,
) : NavKey

@Composable
fun EditProfileScreen(viewModel: EditProfileViewModel) {
    val backStack = LocalNavBackStack.currentOrThrow
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = rememberSnackbarHostState()
    EditProfileContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = backStack::removeLastOrNull,
        onAvatarSelected = viewModel::onAvatarSelected,
        onBannerSelected = viewModel::onBannerSelected,
        onUserNameChanged = viewModel::onUserNameChanged,
        onDescriptionChanged = viewModel::onDescriptionChanged,
        onSaveClick = viewModel::onSaveClick,
    )
    ConsumeSnackbarFlow(snackbarHostState, viewModel.snackBarMessage)
    ConsumeFlow(viewModel.finishScreenFlow) { backStack.removeLastOrNull() }
}

@Composable
private fun EditProfileContent(
    uiState: EditProfileUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onAvatarSelected: (PlatformUri) -> Unit,
    onBannerSelected: (PlatformUri) -> Unit,
    onUserNameChanged: (TextFieldValue) -> Unit,
    onDescriptionChanged: (TextFieldValue) -> Unit,
    onSaveClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            var showConfirmExitDialog by remember { mutableStateOf(false) }
            Toolbar(
                title = stringResource(LocalizedString.bsky_edit_profile_title),
                onBackClick = {
                    if (uiState.modified) {
                        showConfirmExitDialog = true
                    } else {
                        onBackClick()
                    }
                },
                actions = {
                    if (uiState.requesting) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(24.dp)
                        )
                    } else {
                        SimpleIconButton(
                            onClick = {
                                onSaveClick()
                            },
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save",
                        )
                    }
                }
            )
            if (showConfirmExitDialog) {
                AlertConfirmDialog(
                    content = stringResource(LocalizedString.editProfileEditConfirmMessage),
                    onConfirm = onBackClick,
                    onDismissRequest = { showConfirmExitDialog = false }
                )
            }
        },
        snackbarHost = {
            SnackbarHost(
                modifier = Modifier.padding(bottom = 60.dp),
                hostState = snackbarHostState,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(bottom = 30.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
        ) {
            val headerHeight = 150.dp
            val avatarSize = 80.dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight + avatarSize / 2)
            ) {
                HeaderInEdit(
                    uiState = uiState,
                    headerHeight = headerHeight,
                    onHeaderSelected = onBannerSelected,
                )
                AvatarInEdit(
                    uiState = uiState,
                    avatarSize = avatarSize,
                    onAvatarSelected = onAvatarSelected,
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                value = uiState.userName,
                onValueChange = onUserNameChanged,
                placeholder = {
                    Text(
                        modifier = Modifier.alpha(0.7F),
                        text = stringResource(LocalizedString.editProfileInputNameHint)
                    )
                },
                label = {
                    Text(text = stringResource(LocalizedString.editProfileLabelName))
                },
            )
            OutlinedTextField(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                    .fillMaxWidth(),
                value = uiState.description,
                onValueChange = onDescriptionChanged,
                placeholder = {
                    Text(
                        modifier = Modifier.alpha(0.7F),
                        text = stringResource(LocalizedString.editProfileInputNoteHint),
                    )
                },
                label = {
                    Text(text = stringResource(LocalizedString.editProfileLabelNote))
                },
            )
        }
    }
}

@Composable
private fun HeaderInEdit(
    uiState: EditProfileUiState,
    headerHeight: Dp,
    onHeaderSelected: (PlatformUri) -> Unit,
) {
    PickVisualMediaLauncherContainer(
        onResult = { it.firstOrNull()?.let(onHeaderSelected) },
    ) {
        AutoSizeImage(
            url = uiState.bannerLocalUri?.toString() ?: uiState.banner,
            modifier = Modifier
                .height(headerHeight)
                .fillMaxWidth()
                .freadPlaceholder(uiState.banner.isEmpty() && uiState.bannerLocalUri == null)
                .clickable { launchImage() },
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
    }
}

@Composable
private fun BoxScope.AvatarInEdit(
    uiState: EditProfileUiState,
    avatarSize: Dp,
    onAvatarSelected: (PlatformUri) -> Unit,
) {
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(start = 16.dp)
            .size(avatarSize)
            .clip(CircleShape)
            .border(2.dp, Color.White, CircleShape)
            .freadPlaceholder(uiState.avatar.isEmpty() && uiState.avatarLocalUri == null),
    ) {
        AutoSizeImage(
            url = uiState.avatarLocalUri?.toString() ?: uiState.avatar,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = "avatar",
        )
        PickVisualMediaLauncherContainer(
            onResult = {
                it.firstOrNull()?.let(onAvatarSelected)
            },
        ) {
            SimpleIconButton(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5F))
                    .padding(10.dp),
                onClick = { launchImage() },
                imageVector = Icons.Default.Edit,
                tint = Color.White,
                contentDescription = "Edit Avatar",
            )
        }
    }
}
