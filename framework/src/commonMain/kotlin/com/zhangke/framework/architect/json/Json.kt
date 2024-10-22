package com.zhangke.framework.architect.json

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull

val globalJson: Json by lazy {
    Json {
        ignoreUnknownKeys = true
        // use default value if JSON value is null but the property type is non-nullable.
        coerceInputValues = true
    }
}

inline fun <reified T> Json.fromJson(jsonObject: JsonElement): T {
    return decodeFromJsonElement(jsonObject)
}

inline fun <reified T> Json.fromJson(jsonString: String): T {
    return decodeFromString(jsonString)
}

inline fun JsonObject.getStringOrNull(key: String): String? {
    return this.getJsonPrimitiveOrNull(key)?.contentOrNull
}

inline fun JsonObject.getLongOrNull(key: String): Long? {
    return this.getJsonPrimitiveOrNull(key)?.longOrNull
}

inline fun JsonObject.getIntOrNull(key: String): Int? {
    return this.getJsonPrimitiveOrNull(key)?.intOrNull
}

inline fun JsonObject.getJsonPrimitiveOrNull(key: String): JsonPrimitive? {
    return this[key]?.let { it as? JsonPrimitive }
}
