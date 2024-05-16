package com.zhangke.utopia.commonbiz.shared.screen.login.target

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.platform.BlogPlatform
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = LoginToTargetPlatformViewModel.Factory::class)
class LoginToTargetPlatformViewModel @AssistedInject constructor(
    private val statusProvider: StatusProvider,
    @Assisted private val platform: BlogPlatform,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {

        fun create(platform: BlogPlatform): LoginToTargetPlatformViewModel
    }

    fun onServerHostConfirmClick() {
        launchInViewModel {
            statusProvider.accountManager
                .launchAuthBySource(platform.baseUrl)
        }
    }
}
