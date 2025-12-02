package com.zhangke.fread.common.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.architect.theme.dialogScrim
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class BrowserLoadingScreen(
    private val uri: String,
    private val locator: PlatformLocator? = null,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val viewModel = getViewModel<BrowserLoadingViewModel, BrowserLoadingViewModel.Factory> {
            it.create(uri, locator)
        }
        val transparentNavigator = LocalNavigator.currentOrThrow
        ConsumeFlow(viewModel.interceptResultFlow) { result ->
            when (result) {
                is InterceptorResult.CanNotIntercept -> {
                    viewModel.browserLauncher.launchBySystemBrowser(uri)
                    transparentNavigator.pop()
                }

                is InterceptorResult.SuccessWithOpenNewScreen -> {
                    transparentNavigator.pop()
                    GlobalScreenNavigation.navigate(result.screen)
                }
            }
        }
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.dialogScrim),
            contentAlignment = Alignment.Center,
        ) {
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
        LaunchedEffect(Unit) {
            viewModel.startParse()
        }
    }
}

class BrowserLoadingViewModel @Inject constructor(
    private val browserInterceptorSet: Set<BrowserInterceptor>,
    val browserLauncher: BrowserLauncher,
    @Assisted private val uri: String,
    @Assisted private val locator: PlatformLocator?,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(uri: String, locator: PlatformLocator?): BrowserLoadingViewModel
    }

    private val _interceptResultFlow = MutableSharedFlow<InterceptorResult>()
    val interceptResultFlow = _interceptResultFlow.asSharedFlow()

    fun startParse() {
        launchInViewModel {
            val result = browserInterceptorSet.firstNotNullOfOrNull { interceptor ->
                interceptor.intercept(locator, uri) as? InterceptorResult.SuccessWithOpenNewScreen
            }
            if (result == null) {
                _interceptResultFlow.emit(InterceptorResult.CanNotIntercept)
            } else {
                _interceptResultFlow.emit(result)
            }
        }
    }
}
