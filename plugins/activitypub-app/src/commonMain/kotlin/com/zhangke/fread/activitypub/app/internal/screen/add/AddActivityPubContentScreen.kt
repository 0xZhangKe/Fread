package com.zhangke.fread.activitypub.app.internal.screen.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.add_content_title
import com.zhangke.fread.commonbiz.content_add_success
import com.zhangke.fread.commonbiz.content_exist_tips
import com.zhangke.fread.commonbiz.done
import com.zhangke.fread.commonbiz.login
import com.zhangke.fread.framework.skip
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.ui.source.BlogPlatformUi
import com.zhangke.fread.status.ui.source.SourceCommonUi
import org.jetbrains.compose.resources.stringResource

class AddActivityPubContentScreen(private val platform: BlogPlatform) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            getViewModel<AddActivityPubContentViewModel, AddActivityPubContentViewModel.Factory> {
                it.create(platform)
            }
        val uiState by viewModel.uiState.collectAsState()
        AddActivityPubContentContent(
            uiState = uiState,
            onBackClick = navigator::pop,
            onSkipClick = navigator::pop,
            onCompleteClick = navigator::pop,
            onLoginClick = {
                viewModel.onLoginClick()
                navigator.pop()
            },
        )
    }

    @Composable
    private fun AddActivityPubContentContent(
        uiState: LoadableState<AddActivityPubContentUiState>,
        onBackClick: () -> Unit,
        onLoginClick: () -> Unit,
        onSkipClick: () -> Unit,
        onCompleteClick: () -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.add_content_title),
                    onBackClick = onBackClick,
                )
            },
        ) { innerPadding ->
            LoadableLayout(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                state = uiState,
            ) { state ->
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(top = 42.dp)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ContentAddingState(
                        modifier = Modifier.fillMaxWidth(),
                        contentExists = state.contentExist,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PlatformPreview(
                        modifier = Modifier,
                        platform = platform,
                    )
                    LoggedAccountInfo(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        account = state.account,
                        onSkipClick = onSkipClick,
                        onLoginClick = onLoginClick,
                        onDoneClick = onCompleteClick,
                    )
                }
            }
        }
    }

    @Composable
    private fun ContentAddingState(
        modifier: Modifier,
        contentExists: Boolean,
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (contentExists) {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.AutoMirrored.Filled.StickyNote2,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(Res.string.content_exist_tips),
                    modifier = Modifier.padding(top = 8.dp),
                )
            } else {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(Res.string.content_add_success),
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }

    @Composable
    private fun PlatformPreview(
        modifier: Modifier,
        platform: BlogPlatform,
    ) {
        Card(
            modifier = modifier.fillMaxWidth(),
        ) {
            BlogPlatformUi(
                modifier = Modifier,
                platform = platform,
                showDivider = false,
            )
        }
    }

    @Composable
    private fun LoggedAccountInfo(
        modifier: Modifier,
        account: ActivityPubLoggedAccount?,
        onSkipClick: () -> Unit,
        onLoginClick: () -> Unit,
        onDoneClick: () -> Unit,
    ) {
        if (account == null) {
            Row(
                modifier = modifier.padding(horizontal = 64.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = onSkipClick,
                    colors = ButtonDefaults.textButtonColors(),
                ) {
                    Text(text = stringResource(com.zhangke.fread.framework.Res.string.skip))
                }
                Spacer(modifier = Modifier.width(32.dp))
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = onLoginClick,
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                ) {
                    Text(text = stringResource(Res.string.login))
                }
            }
        } else {
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SourceCommonUi(
                    modifier = Modifier.fillMaxWidth(),
                    thumbnail = account.avatar.orEmpty(),
                    title = account.userName,
                    subtitle = account.prettyHandle,
                    description = account.description.orEmpty(),
                    protocolLogo = null,
                    showDivider = false,
                )

                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = onDoneClick,
                ) {
                    Text(text = stringResource(Res.string.done))
                }
            }
        }
    }
}
