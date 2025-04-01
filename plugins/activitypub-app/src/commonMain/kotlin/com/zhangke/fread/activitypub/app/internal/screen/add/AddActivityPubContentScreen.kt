package com.zhangke.fread.activitypub.app.internal.screen.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
                        .padding(top = 36.dp)
                        .padding(horizontal = 16.dp),
                ) {
                    ContentAddingState(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        contentExists = state.contentExist,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PlatformPreview(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        platform = platform,
                    )
                    LoggedAccountInfo(
                        modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(),
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
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Outlined.Error,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                )
                Text(
                    text = stringResource(Res.string.content_exist_tips),
                    modifier = Modifier.padding(top = 8.dp),
                )
            } else {
                Icon(
                    modifier = Modifier.size(48.dp),
                    imageVector = Icons.Outlined.Check,
                    tint = MaterialTheme.colorScheme.primary,
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
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Row(
                modifier = modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onSkipClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                ) {
                    Text(text = stringResource(com.zhangke.fread.framework.Res.string.skip))
                }
                Spacer(modifier = Modifier.width(32.dp))
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                ) {
                    Text(text = stringResource(Res.string.login))
                }
            }
        } else {
            VerticalDivider(modifier = Modifier.padding(start = 40.dp).height(48.dp))
            Column(
                modifier = modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                }

                Button(
                    modifier = Modifier.padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    onClick = onDoneClick,
                ) {
                    Text(text = stringResource(Res.string.done))
                }
            }
        }
    }
}
