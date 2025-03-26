package com.zhangke.fread.bluesky

import com.zhangke.framework.architect.coroutines.ApplicationScope
import com.zhangke.framework.module.ModuleStartup
import com.zhangke.fread.bluesky.internal.usecase.RefreshSessionUseCase
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class BskyStartup @Inject constructor(
    private val refreshSession: RefreshSessionUseCase,
) : ModuleStartup {

    override fun onAppCreate() {
        ApplicationScope.launch {
            refreshSession()
        }
    }
}
