package com.zhangke.fread.common.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.architect.theme.dialogScrim
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.composable.SelectableAccount
import com.zhangke.fread.common.deeplink.SelectAccountForPublishScreen
import com.zhangke.fread.common.deeplink.SelectedContentSwitcher
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusProviderProtocol
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.stringResource

class UrlRedirectScreen(
    private val uri: String,
    private val locator: PlatformLocator? = null,
    private val isFromExternal: Boolean = false,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val viewModel = getViewModel<UrlRedirectViewModel, UrlRedirectViewModel.Factory> {
            it.create(uri, locator, isFromExternal)
        }
        val transparentNavigator = LocalNavigator.currentOrThrow
        val browserLauncher = LocalActivityBrowserLauncher.current
        ConsumeFlow(viewModel.finishPageFlow) { transparentNavigator.pop() }
        ConsumeFlow(viewModel.openNewPageFlow) { GlobalScreenNavigation.navigate(it) }
        ConsumeFlow(viewModel.finishAndOpenUrlTab) {
            transparentNavigator.pop()
            browserLauncher.launchWebTabInApp(
                url = uri,
                locator = null,
                checkAppSupportPage = false,
            )
        }
        ConsumeFlow(viewModel.finishAndOpenPublishScreen) {
            transparentNavigator.pop()
            GlobalScreenNavigation.navigateByTransparent(SelectAccountForPublishScreen(uri))
        }
        val pageState by viewModel.pageState.collectAsState()
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.dialogScrim),
            contentAlignment = Alignment.Center,
        ) {
            when (pageState) {
                is UrlRedirectPageState.Loading -> {
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

                is UrlRedirectPageState.SelectAccount -> {
                    val accountList = (pageState as UrlRedirectPageState.SelectAccount).accounts
                    Surface(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 2.dp,
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
                                    onClick = { viewModel.onAccountSelected(account) },
                                )
                            }
                        }
                    }
                }
            }
        }
        LaunchedEffect(Unit) { viewModel.onPageResumed() }
    }
}

sealed interface UrlRedirectPageState {

    data object Loading : UrlRedirectPageState

    data class SelectAccount(val accounts: List<LoggedAccount>) : UrlRedirectPageState
}

class UrlRedirectViewModel @Inject constructor(
    private val browserInterceptorSet: Set<BrowserInterceptor>,
    val browserLauncher: BrowserLauncher,
    private val statusProvider: StatusProvider,
    private val selectedContentSwitcher: SelectedContentSwitcher,
    @Assisted private val uri: String,
    @Assisted private val locator: PlatformLocator?,
    @Assisted private val isFromExternal: Boolean,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            uri: String,
            locator: PlatformLocator?,
            isFromExternal: Boolean,
        ): UrlRedirectViewModel
    }

    private val _openNewPageFlow = MutableSharedFlow<Screen>()
    val openNewPageFlow = _openNewPageFlow.asSharedFlow()

    private val _finishPageFlow = MutableSharedFlow<Unit>()
    val finishPageFlow = _finishPageFlow.asSharedFlow()

    private val _finishAndOpenUrlTab = MutableSharedFlow<String>()
    val finishAndOpenUrlTab = _finishAndOpenUrlTab.asSharedFlow()

    private val _finishAndOpenPublishScreen = MutableSharedFlow<String>()
    val finishAndOpenPublishScreen = _finishAndOpenPublishScreen.asSharedFlow()

    private val _pageState = MutableStateFlow<UrlRedirectPageState>(UrlRedirectPageState.Loading)
    val pageState = _pageState.asStateFlow()

    private var accountSelectCount = 0

    fun onPageResumed() {
        parseUrl(locator)
    }

    fun onAccountSelected(account: LoggedAccount) {
        parseUrl(account.locator)
    }

    private fun parseUrl(locator: PlatformLocator?) {
        launchInViewModel {
            _pageState.emit(UrlRedirectPageState.Loading)
            val result = browserInterceptorSet.firstNotNullOfOrNull { interceptor ->
                interceptor.intercept(
                    locator = locator,
                    url = uri,
                    isFromExternal = isFromExternal,
                ).takeIf { it !is InterceptorResult.CanNotIntercept }
            }
            if (result == null) {
                if (isFromExternal) {
                    _finishAndOpenPublishScreen.emit(uri)
                } else {
                    _finishAndOpenUrlTab.emit(uri)
                }
            } else {
                when (result) {
                    is InterceptorResult.SuccessWithOpenNewScreen -> {
                        _finishPageFlow.emit(Unit)
                        _openNewPageFlow.emit(result.screen)
                    }

                    is InterceptorResult.SwitchHomeContent -> {
                        selectedContentSwitcher.switchToContent(result.content)
                        _finishPageFlow.emit(Unit)
                    }

                    is InterceptorResult.RequireSelectAccount -> {
                        if (accountSelectCount > 1) {
                            _finishPageFlow.emit(Unit)
                        } else {
                            accountSelectCount++
                            val accounts = loadLoggedAccounts(result.protocol)
                            if (accounts.isEmpty()) {
                                _finishPageFlow.emit(Unit)
                            } else {
                                _pageState.emit(UrlRedirectPageState.SelectAccount(accounts))
                            }
                        }
                    }

                    else -> {
                        _finishPageFlow.emit(Unit)
                    }
                }
            }
        }
    }

    private suspend fun loadLoggedAccounts(protocol: StatusProviderProtocol): List<LoggedAccount> {
        return statusProvider.accountManager
            .getAllLoggedAccount()
            .filter { it.platform.protocol == protocol }
    }
}
