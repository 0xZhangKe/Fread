package com.zhangke.fread.common.alttext

/**
 * Resizes an image so its longest side is at most [maxLongestSide] px and re-encodes
 * it as JPEG at [quality]. The result is base64-encoded for use in an `image_url`
 * `data:` URL.
 */
expect fun resizeAndJpegBase64(
    bytes: ByteArray,
    maxLongestSide: Int = 1024,
    quality: Int = 85,
): String
