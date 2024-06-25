package com.zhangke.fread.commonbiz.shared.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.voyager.pushDestination
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.platform.BlogPlatform
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class PostStatusIntermediaryScreen : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: PostStatusIntermediaryViewModel = getViewModel()
        val loadableState by viewModel.platform.collectAsState()
        when (loadableState) {
            is LoadableState.Failed -> LaunchedEffect(Unit) {
                navigator.pop()
            }

            is LoadableState.Success -> LaunchedEffect(loadableState) {
                navigator.pop()
                viewModel.screenProvider
                    .getPostStatusScreen(loadableState.requireSuccessData())
                    ?.let(navigator::pushDestination)
            }

            else -> {}
        }
    }
}

@HiltViewModel
class PostStatusIntermediaryViewModel @Inject constructor(
    private val statusProvider: StatusProvider
) : ViewModel() {

    private val _platform = MutableStateFlow<LoadableState<BlogPlatform>>(LoadableState.Loading())
    val platform: StateFlow<LoadableState<BlogPlatform>> = _platform.asStateFlow()

    val screenProvider get() = statusProvider.screenProvider

    init {
        launchInViewModel {
            val postToPlatform = statusProvider.accountManager
                .getAllLoggedAccount()
                .firstOrNull()
                ?.platform
            if (postToPlatform == null) {
                _platform.value = LoadableState.Failed(RuntimeException("Not login"))
            } else {
                _platform.value = LoadableState.success(postToPlatform)
            }
        }
    }
}
