package com.zhangke.framework.architect.json

import kotlinx.serialization.modules.SerializersModuleBuilder

interface JsonModuleBuilder {

    fun SerializersModuleBuilder.buildSerializersModule()
}
