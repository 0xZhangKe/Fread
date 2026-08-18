package com.zhangke.fread.common.translate

import com.zhangke.fread.common.translate.PostTranslatingContent.MediaAltTranslatingContent
import com.zhangke.fread.status.richtext.translate.TranslatorBlock

sealed class PostTranslateState {

    data object Idle : PostTranslateState()

    data object Translating : PostTranslateState()

    data class Translated(
        val translatedContent: String,
    ) : PostTranslateState()

    data class Failed(val error: Throwable) : PostTranslateState()
}

data class TranslatedPost(
    val spoiler: List<TranslatorBlock>?,
    val content: List<TranslatorBlock>?,
    val title: String?,
    val medias: List<MediaAltTranslatingContent>?,
    val poll: List<String>?,
)
