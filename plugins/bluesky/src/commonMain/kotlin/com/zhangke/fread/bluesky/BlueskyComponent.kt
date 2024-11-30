package com.zhangke.fread.bluesky

import com.zhangke.fread.status.IStatusProvider
import me.tatarka.inject.annotations.IntoSet
import me.tatarka.inject.annotations.Provides

interface BlueskyComponent {

    @IntoSet
    @Provides
    fun provideActivityPubProvider(blueskyProvider: BlueskyProvider): IStatusProvider {
        return blueskyProvider
    }
}
