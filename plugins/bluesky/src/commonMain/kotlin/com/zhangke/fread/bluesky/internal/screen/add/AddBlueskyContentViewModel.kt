package com.zhangke.fread.bluesky.internal.screen.add

import androidx.lifecycle.ViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.common.di.ViewModelFactory
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class AddBlueskyContentViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    @Assisted private val baseUrl: FormalBaseUrl,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            baseUrl: FormalBaseUrl,
        ): AddBlueskyContentViewModel
    }

}
