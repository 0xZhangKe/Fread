package com.zhangke.framework.utils

expect class ImageCompressUtils() {

    fun compress(bytes: ByteArray, targetSize: StorageSize): CompressResult
}

data class CompressResult(
    val bytes: ByteArray,
    val ratio: AspectRatio?,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as CompressResult

        if (!bytes.contentEquals(other.bytes)) return false
        if (ratio != other.ratio) return false

        return true
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + ratio.hashCode()
        return result
    }
}
