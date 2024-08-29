package com.zhangke.framework.utils

import org.junit.Test

class StorageSizeTest {

    @Test
    fun test() {
        val bytes = 1L * 1024 * 1024 * 1024
        val storageSize = StorageSize(bytes)
        println(storageSize.length)
        println(storageSize.KB)
        println(storageSize.MB)
        println(storageSize.GB)
    }
}