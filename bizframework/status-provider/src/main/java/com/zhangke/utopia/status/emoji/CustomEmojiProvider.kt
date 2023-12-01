package com.zhangke.utopia.status.emoji

import com.zhangke.framework.collections.mapFirst
import com.zhangke.utopia.status.platform.BlogPlatform

class CustomEmojiProvider(
    private val providerList: List<ICustomEmojiProvider>,
) {

    suspend fun getCustomEmojiList(platform: BlogPlatform): Result<List<CustomEmoji>>{
        return providerList.mapFirst {
            it.getCustomEmojiList(platform)
        }
    }
}

interface ICustomEmojiProvider{

    /**
     * @return null if platform not compatible
     */
    suspend fun getCustomEmojiList(platform: BlogPlatform): Result<List<CustomEmoji>>?
}
