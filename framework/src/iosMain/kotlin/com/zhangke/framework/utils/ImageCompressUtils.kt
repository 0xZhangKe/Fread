package com.zhangke.framework.utils

actual class ImageCompressUtils {

    actual fun compress(bytes: ByteArray, targetSize: StorageSize): CompressResult {
        return CompressResult(bytes, null)
    }
}