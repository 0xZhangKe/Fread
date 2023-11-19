package com.zhangke.utopia.profile.pages.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.utopia.common.feeds.usecase.QueryAllPlatformByLocalSourceUseCase
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val queryAllPlatform: QueryAllPlatformByLocalSourceUseCase,
) : ViewModel() {

    init {
        viewModelScope.launch {
            queryAllPlatform()
                .onSuccess {

                }.onFailure {

                }
        }
    }
}
