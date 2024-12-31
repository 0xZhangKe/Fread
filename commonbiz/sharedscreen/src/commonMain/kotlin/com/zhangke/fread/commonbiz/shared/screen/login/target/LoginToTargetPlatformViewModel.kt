package com.zhangke.fread.commonbiz.shared.screen.login.target

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class LoginToTargetPlatformViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    @Assisted private val platform: BlogPlatform,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(platform: BlogPlatform): LoginToTargetPlatformViewModel
    }

    fun onServerHostConfirmClick(openOauthPage: (String) -> Unit) {
        launchInViewModel {
            statusProvider.accountManager
                .triggerAuthBySource(platform.baseUrl, openOauthPage)
        }
    }
}
