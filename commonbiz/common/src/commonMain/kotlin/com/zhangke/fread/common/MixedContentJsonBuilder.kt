package com.zhangke.fread.common

import com.zhangke.framework.architect.json.JsonModuleBuilder
import com.zhangke.fread.status.content.MixedContent
import com.zhangke.fread.status.model.FreadContent
import com.zhangke.krouter.annotation.Service
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.serializer

@Service
class MixedContentJsonBuilder : JsonModuleBuilder {

    override fun SerializersModuleBuilder.buildSerializersModule() {
        polymorphic(
            baseClass = FreadContent::class,
            actualClass = MixedContent::class,
            actualSerializer = serializer(),
        )
    }
}
