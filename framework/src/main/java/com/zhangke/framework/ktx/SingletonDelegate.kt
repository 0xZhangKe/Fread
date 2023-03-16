package com.zhangke.framework.ktx

import kotlin.reflect.KProperty

private val singletonHolder = mutableMapOf<String, Any>()

@Suppress("FunctionName")
inline fun <reified T> SingletonDelegate(noinline creator: (thisRef: Any?) -> T): SingletonTypedDelegate<T> {
    return SingletonTypedDelegate(T::class.java, creator)
}

class SingletonTypedDelegate<T>(private val type: Class<T>, private val creator: (Any?) -> T) {

    @Suppress("UNCHECKED_CAST")
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return singletonHolder.getOrPut(buildKey(thisRef)) {
            creator(thisRef) as Any
        } as T
    }

    private fun buildKey(thisRef: Any?): String {
        return if (thisRef != null) {
            "${thisRef::class.java.canonicalName}@${thisRef.hashCode()}" +
                    "_${type::class.java.canonicalName}"
        } else {
            type::class.java.canonicalName!!
        }
    }
}