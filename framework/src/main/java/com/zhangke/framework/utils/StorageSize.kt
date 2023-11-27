package com.zhangke.framework.utils

@Suppress("PropertyName")
@JvmInline
value class StorageSize(val length: Long) {

    val KB: Double get() = length.toDouble() / 1024

    val MB: Double get() = KB / 1024

    val GB: Double get() = MB / 1024
}
