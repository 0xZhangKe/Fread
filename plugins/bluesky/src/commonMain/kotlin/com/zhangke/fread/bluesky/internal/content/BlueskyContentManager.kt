package com.zhangke.fread.bluesky.internal.content

import com.zhangke.fread.status.content.AddContentAction
import com.zhangke.fread.status.content.IContentManager
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.platform.BlogPlatform
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer
import me.tatarka.inject.annotations.Inject

class BlueskyContentManager @Inject constructor() : IContentManager {

    override suspend fun addContent(
        platform: BlogPlatform,
        action: AddContentAction
    ) {

    }

    override fun SerializersModuleBuilder.buildSerializersModule() {
        polymorphic(
            baseClass = FreadContent::class,
            actualClass = BlueskyContent::class,
            actualSerializer = serializer(),
        )
    }

    override fun restoreContent(config: ContentConfig): FreadContent? {
        // Bluesky does not container any old content data
        return null
    }
}
