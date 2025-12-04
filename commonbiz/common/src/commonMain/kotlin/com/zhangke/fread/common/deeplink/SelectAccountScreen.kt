package com.zhangke.fread.common.deeplink

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.seiko.imageloader.model.ImageAction
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.rememberImageActionPainter
import com.seiko.imageloader.ui.AutoSizeBox
import com.zhangke.framework.architect.theme.dialogScrim
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.freadPlaceholder
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.composable.SelectableAccount
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

class SelectAccountForPublishScreen(
    private val text: String,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val viewModel = getViewModel<SelectAccountForPublishViewModel>()
        val loadingAccountList by viewModel.loggedAccounts.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.pop()
            GlobalScreenNavigation.navigate(it)
        }
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.dialogScrim)
                .padding(horizontal = 32.dp)
                .noRippleClick { navigator.pop() },
            contentAlignment = Alignment.Center,
        ) {
            when (loadingAccountList) {
                is LoadableState.Loading -> {
                    Surface(
                        modifier = Modifier,
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(vertical = 24.dp, horizontal = 64.dp)
                                .size(80.dp)
                        )
                    }
                }

                is LoadableState.Success -> {
                    val accountList = loadingAccountList.requireSuccessData()
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 6.dp,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(16.dp)
                                .verticalScroll(rememberScrollState()),
                        ) {
                            Text(
                                modifier = Modifier,
                                text = stringResource(LocalizedString.statusUiSwitchAccountDialogTitle),
                                style = MaterialTheme.typography.titleMedium
                                    .copy(fontWeight = FontWeight.SemiBold),
                            )
                            for (account in accountList) {
                                Spacer(modifier = Modifier.height(16.dp))
                                SelectableAccount(
                                    account = account,
                                    onClick = {
                                        viewModel.onAccountSelected(it, text)
                                    },
                                )
                            }
                        }
                    }
                }

                else -> {
                    navigator.pop()
                }
            }
        }
    }

}

class SelectAccountForPublishViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _loggedAccounts =
        MutableStateFlow<LoadableState<List<LoggedAccount>>>(LoadableState.idle())
    val loggedAccounts = _loggedAccounts.asStateFlow()

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    init {
        launchInViewModel {
            _loggedAccounts.emit(LoadableState.loading())
            val accounts = statusProvider.accountManager.getAllLoggedAccount()
            _loggedAccounts.emit(LoadableState.success(accounts))
        }
    }

    fun onAccountSelected(account: LoggedAccount, text: String) {
        statusProvider.screenProvider
            .getPublishScreen(account, text)
            ?.let { launchInViewModel { _openScreenFlow.emit(it) } }
    }
}
