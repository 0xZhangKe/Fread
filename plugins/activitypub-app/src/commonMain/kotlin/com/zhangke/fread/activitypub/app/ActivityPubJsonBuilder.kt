package com.zhangke.fread.activitypub.app

import com.zhangke.framework.architect.json.JsonModuleBuilder
import com.zhangke.fread.activitypub.app.internal.content.ActivityPubContent
import com.zhangke.fread.activitypub.app.internal.richtext.ActivityPubRichText
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.fread.status.richtext.IRichText
import com.zhangke.krouter.annotation.Service
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer

@Service
class ActivityPubJsonBuilder : JsonModuleBuilder {

    override fun SerializersModuleBuilder.buildSerializersModule() {
        polymorphic(
            baseClass = FreadContent::class,
            actualClass = ActivityPubContent::class,
            actualSerializer = serializer(),
        )
        polymorphic(
            baseClass = IRichText::class,
            actualClass = ActivityPubRichText::class,
            actualSerializer = serializer(),
        )
    }
}
