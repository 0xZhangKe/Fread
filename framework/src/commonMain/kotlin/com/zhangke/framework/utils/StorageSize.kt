package com.zhangke.framework.utils

import kotlin.jvm.JvmInline

@Suppress("PropertyName")
@JvmInline
value class StorageSize(val length: Long) {

    val KB: Double get() = length.toDouble() / 1024

    val MB: Double get() = KB / 1024

    val GB: Double get() = MB / 1024
}

val StorageSize.prettyString: String
    get() {
        if (GB >= 1) {
            return "${GB.decimal(2)} GB"
        }
        if (MB >= 1) {
            return "${MB.decimal(2)} MB"
        }
        return "${KB.decimal(2)} KB"
    }
