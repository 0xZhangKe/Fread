package com.zhangke.fread.common.alttext

actual fun resizeAndJpegBase64(
    bytes: ByteArray,
    maxLongestSide: Int,
    quality: Int,
): String {
    // TODO(iOS): Implement via UIImage / UIGraphicsImageRenderer + UIImageJPEGRepresentation.
    error("Alt text generation is not yet implemented on iOS.")
}
