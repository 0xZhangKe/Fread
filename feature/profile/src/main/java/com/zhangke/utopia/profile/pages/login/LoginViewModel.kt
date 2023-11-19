package com.zhangke.utopia.profile.pages.login

import androidx.lifecycle.ViewModel
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val provider: StatusProvider,
): ViewModel() {

    init {
//        provider.accountManager.getAllLoggedAccount()
    }
}
