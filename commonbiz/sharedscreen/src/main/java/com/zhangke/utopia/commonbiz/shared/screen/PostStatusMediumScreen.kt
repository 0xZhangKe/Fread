package com.zhangke.utopia.commonbiz.shared.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.voyager.tryPush
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.platform.BlogPlatform
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostStatusMediumScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: PostStatusMediumViewModel = getViewModel()
        val platform by viewModel.platform.collectAsState()
        if (platform != null) {
            LaunchedEffect(platform) {
                viewModel.screenProvider
                    .getPostStatusScreen(platform!!)
                    ?.let(navigator::tryPush)
                navigator.pop()
            }
        } else {
            LaunchedEffect(Unit) {
                navigator.pop()
            }
        }
    }
}

class PostStatusMediumViewModel(
    private val statusProvider: StatusProvider
) : ViewModel() {

    private val _platform = MutableStateFlow<BlogPlatform?>(null)
    val platform: StateFlow<BlogPlatform?> get() = _platform.asStateFlow()

    val screenProvider get() = statusProvider.screenProvider

    init {
        launchInViewModel {
            _platform.value = statusProvider.accountManager
                .getActiveAccountList()
                .firstOrNull()
                ?.platform
        }
    }
}
