package com.zhangke.fread.activitypub.app.internal.screen.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StickyNote2
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.add_content_success_with_account
import com.zhangke.fread.commonbiz.add_content_success_with_login_reminder
import com.zhangke.fread.commonbiz.add_content_title
import com.zhangke.fread.commonbiz.content_exist_tips
import com.zhangke.fread.commonbiz.login
import com.zhangke.fread.framework.skip
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.ui.BlogAuthorAvatar
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
                    if (state.contentExist) {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.AutoMirrored.Filled.StickyNote2,
                            contentDescription = null,
                        )
                        Text(
                            text = stringResource(Res.string.content_exist_tips),
                            modifier = Modifier.padding(top = 16.dp),
                        )
                    } else {
                        Icon(
                            modifier = Modifier,
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                        )
                        if (state.account == null) {
                            Text(
                                text = stringResource(Res.string.add_content_success_with_login_reminder),
                                modifier = Modifier.padding(top = 8.dp),
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                            ) {
                                Button(
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                        contentColor = MaterialTheme.colorScheme.onSurface,
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    onClick = onSkipClick,
                                ) {
                                    Text(stringResource(com.zhangke.fread.framework.Res.string.skip))
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Button(
                                    shape = RoundedCornerShape(6.dp),
                                    onClick = onLoginClick,
                                ) {
                                    Text(stringResource(Res.string.login))
                                }
                            }
                        } else {
                            Text(
                                text = stringResource(Res.string.add_content_success_with_account),
                                modifier = Modifier.padding(top = 8.dp),
                            )
                            BlogAuthorAvatar(
                                modifier = Modifier.size(48.dp).padding(top = 8.dp),
                                imageUrl = state.account.avatar,
                            )
                            Text(
                                text = state.account.userName,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    shape = RoundedCornerShape(6.dp),
                                    onClick = onCompleteClick,
                                ) {
                                    Text(stringResource(Res.string.login))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
