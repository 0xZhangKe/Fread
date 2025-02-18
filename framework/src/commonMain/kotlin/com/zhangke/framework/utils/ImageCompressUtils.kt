package com.zhangke.framework.utils

expect class ImageCompressUtils() {

    fun compress(bytes: ByteArray, targetSize: StorageSize): ByteArray
}
