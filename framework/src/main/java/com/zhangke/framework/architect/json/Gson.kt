package com.zhangke.framework.architect.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.ParameterizedType

val globalGson: Gson by lazy {
    GsonBuilder()
        .registerTypeAdapterFactory(EnumTypeAdapterFactory())
        .create()
}

inline fun <reified T> Gson.fromJson(jsonObject: JsonObject): T {
    return this.fromJson(jsonObject, T::class.java)
}

inline fun <reified T> Gson.fromJson(jsonString: String): T {
    return this.fromJson(jsonString, T::class.java)
}

private class EnumTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val rawType = type.rawType
        if (Enum::class.java.isAssignableFrom(rawType) && JsonEnum::class.java.isAssignableFrom(
                rawType
            )
        ) {
            @Suppress("UNCHECKED_CAST")
            return EnumWithJsonValueTypeAdapter(gson, rawType) as TypeAdapter<T>
        }
        return null
    }
}

private class EnumWithJsonValueTypeAdapter(val gson: Gson, val rawType: Class<*>) :
    TypeAdapter<Any>() {
    override fun write(out: JsonWriter, value: Any?) {
        if (value == null) {
            out.nullValue()
            return
        }
        val jsonValueObject = (value as JsonEnum<*>).value
        if (jsonValueObject == null) {
            out.nullValue()
            return
        }
        val adapter = gson.getAdapter(jsonValueObject.javaClass)
        adapter.write(out, jsonValueObject)
    }

    override fun read(`in`: JsonReader): Any? {
        val genericInterface = rawType.genericInterfaces.first {
            JsonEnum::class.java == `$Gson$Types`.getRawType(it)
        }
        val valueType = (genericInterface as ParameterizedType).actualTypeArguments[0]
        val adapter = gson.getAdapter(TypeToken.get(valueType))
        val jsonValueObject = adapter.read(`in`)
        return rawType.enumConstants?.firstOrNull { (it as JsonEnum<*>).value == jsonValueObject }
    }
}

interface JsonEnum<T> {
    val value: T
}
