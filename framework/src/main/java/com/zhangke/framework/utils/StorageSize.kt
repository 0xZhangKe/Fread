package com.zhangke.framework.utils

@Suppress("PropertyName")
@JvmInline
value class StorageSize(val bytes: Long) {

    val KB: Long get() = bytes / 1024

    val MB: Long get() = KB / 1024

    val GB: Long get() = MB / 1024
}
