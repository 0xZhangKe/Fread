package com.zhangke.utopia.feeds.pages.home

import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.status.platform.BlogPlatform

data class FeedsHomeUiState(
    val selectedIndex: Int,
    val feedsConfigList: List<FeedsConfigWithPlatforms>,
)

data class FeedsConfigWithPlatforms(
    val feedsConfig: FeedsConfig,
    val platformList: List<BlogPlatform>,
)
