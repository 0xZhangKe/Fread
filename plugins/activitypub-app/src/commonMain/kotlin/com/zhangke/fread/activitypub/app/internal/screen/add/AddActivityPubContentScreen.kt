package com.zhangke.fread.activitypub.app.internal.screen.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.add_content_title
import com.zhangke.fread.commonbiz.content_add_success
import com.zhangke.fread.commonbiz.login
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.ui.source.BlogPlatformUi
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
        AddActivityPubContentContent(
            onBackClick = navigator::pop,
            onLoginClick = {
                viewModel.onLoginClick()
                navigator.pop()
            },
        )
    }

    @Composable
    private fun AddActivityPubContentContent(
        onBackClick: () -> Unit,
        onLoginClick: () -> Unit,
    ) {
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.add_content_title),
                    onBackClick = onBackClick,
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(innerPadding)
                    .padding(top = 36.dp)
                    .padding(horizontal = 16.dp),
            ) {
                ContentAddingState(Modifier.align(Alignment.CenterHorizontally).fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                PlatformPreview(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    platform = platform,
                )
                Spacer(modifier = Modifier.padding(top = 16.dp))
                Button(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                ) {
                    Text(text = stringResource(Res.string.login))
                }
            }
        }
    }

    @Composable
    private fun ContentAddingState(modifier: Modifier) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
}
