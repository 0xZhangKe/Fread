package com.zhangke.fread.activitypub.app.internal.screen.account

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil.compose.AsyncImage
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.IconButtonStyle
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.StyledIconButton
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.utils.buildPickVisualImageRequest
import com.zhangke.framework.utils.rememberSinglePickVisualMediaLauncher
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.SharedFlow

class EditAccountInfoScreen(
    private val accountUri: FormalUri,
) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<EditAccountInfoViewModel, EditAccountInfoViewModel.Factory>() {
            it.create(accountUri)
        }
        val uiState by viewModel.uiState.collectAsState()
        EditAccountInfoContent(
            uiState = uiState,
            snackBarMessageFlow = viewModel.snackBarMessageFlow,
            onBackClick = navigator::pop,
            onEditClick = viewModel::onEditClick,
            onUserNameChanged = viewModel::onUserNameInput,
            onBioChanged = viewModel::onUserDescriptionInput,
            onFieldChanged = viewModel::onFieldInput,
            onFieldDeleteClick = viewModel::onFieldDelete,
            onFieldAddClick = viewModel::onFieldAddClick,
            onAvatarSelected = viewModel::onAvatarSelected,
            onHeaderSelected = viewModel::onHeaderSelected,
        )
        ConsumeFlow(viewModel.finishPageFlow) {
            navigator.pop()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun EditAccountInfoContent(
        uiState: EditAccountUiState,
        snackBarMessageFlow: SharedFlow<TextString>,
        onBackClick: () -> Unit,
        onEditClick: () -> Unit,
        onUserNameChanged: (String) -> Unit,
        onBioChanged: (String) -> Unit,
        onFieldChanged: (Int, String, String) -> Unit,
        onFieldDeleteClick: (Int) -> Unit,
        onFieldAddClick: () -> Unit,
        onAvatarSelected: (Uri) -> Unit,
        onHeaderSelected: (Uri) -> Unit
    ) {
        val snackBarHost = rememberSnackbarHostState()
        ConsumeSnackbarFlow(snackBarHost, snackBarMessageFlow)
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackBarHost)
            },
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        SimpleIconButton(
                            onClick = onBackClick,
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                        )
                    },
                    title = {
                        Text(text = uiState.name)
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
                                onClick = onEditClick,
                                imageVector = Icons.Default.Check,
                                contentDescription = "Edit",
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(bottom = 30.dp)
                    .verticalScroll(rememberScrollState()),
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
                        onHeaderSelected = onHeaderSelected,
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
                        .fillMaxWidth()
                        .freadPlaceholder(uiState.name.isEmpty()),
                    value = uiState.name,
                    onValueChange = onUserNameChanged,
                    label = {
                        Text(text = stringResource(R.string.activity_pub_edit_account_info_label_name))
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                        .freadPlaceholder(uiState.description.isEmpty()),
                    value = uiState.description,
                    onValueChange = onBioChanged,
                    label = {
                        Text(text = stringResource(R.string.activity_pub_edit_account_info_label_note))
                    }
                )
                AccountFieldListUi(
                    uiState = uiState,
                    onFieldChanged = onFieldChanged,
                    onFieldDeleteClick = onFieldDeleteClick,
                    onFieldAddClick = onFieldAddClick,
                )
            }
        }
    }

    @Composable
    private fun AccountFieldListUi(
        uiState: EditAccountUiState,
        onFieldChanged: (Int, String, String) -> Unit,
        onFieldDeleteClick: (Int) -> Unit,
        onFieldAddClick: () -> Unit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.activity_pub_edit_account_info_label_about),
                style = MaterialTheme.typography.headlineSmall,
            )
            Box(modifier = Modifier.weight(1F))
            if (uiState.fieldAddable) {
                StyledIconButton(
                    onClick = onFieldAddClick,
                    imageVector = Icons.Default.Add,
                    style = IconButtonStyle.STANDARD,
                    contentDescription = "Add",
                )
            }
        }
        Box(modifier = Modifier.height(6.dp))
        uiState.fieldList.forEach { fieldUiState ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 11.dp, end = 8.dp, bottom = 11.dp),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1F)
                        .padding(end = 8.dp)
                ) {

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = fieldUiState.name,
                        onValueChange = { onFieldChanged(fieldUiState.idForUi, it, fieldUiState.value) },
                    )

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        value = fieldUiState.value,
                        onValueChange = { onFieldChanged(fieldUiState.idForUi, fieldUiState.name, it) },
                    )
                }

                SimpleIconButton(
                    onClick = { onFieldDeleteClick(fieldUiState.idForUi) },
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                )
            }
        }
    }

    @Composable
    private fun HeaderInEdit(
        uiState: EditAccountUiState,
        headerHeight: Dp,
        onHeaderSelected: (Uri) -> Unit
    ) {
        val launcher = rememberSinglePickVisualMediaLauncher(
            onResult = { onHeaderSelected(it) },
        )
        AsyncImage(
            modifier = Modifier
                .height(headerHeight)
                .fillMaxWidth()
                .freadPlaceholder(uiState.header.isEmpty())
                .clickable {
                    launcher.launch(buildPickVisualImageRequest())
                },
            model = uiState.header,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )
    }

    @Composable
    private fun BoxScope.AvatarInEdit(
        uiState: EditAccountUiState,
        avatarSize: Dp,
        onAvatarSelected: (Uri) -> Unit,
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp)
                .size(avatarSize)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape)
                .freadPlaceholder(uiState.avatar.isEmpty())
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = uiState.avatar,
                contentScale = ContentScale.Crop,
                contentDescription = "avatar",
            )
            val launcher = rememberSinglePickVisualMediaLauncher(
                onResult = { onAvatarSelected(it) },
            )
            SimpleIconButton(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5F))
                    .padding(10.dp),
                onClick = {
                    launcher.launch(
                        buildPickVisualImageRequest()
                    )
                },
                imageVector = Icons.Default.Edit,
                tint = Color.White,
                contentDescription = "Edit Avatar",
            )
        }
    }
}
