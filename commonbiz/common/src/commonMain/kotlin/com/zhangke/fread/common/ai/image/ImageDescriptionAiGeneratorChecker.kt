package com.zhangke.fread.common.ai.image

expect class ImageDescriptionAiGeneratorChecker() {

    fun available(): Boolean
}

fun imageAltGeneratorAvailable(): Boolean {
    return ImageDescriptionAiGeneratorChecker().available()
}
