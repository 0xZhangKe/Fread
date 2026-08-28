package com.zhangke.fread.common.translate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.zhangke.fread.common.handler.TextHandler
import com.zhangke.fread.common.translate.PostTranslatingContent.MediaAltTranslatingContent
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.richtext.RichText
import com.zhangke.fread.status.richtext.translate.TranslatorBlock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import org.koin.compose.getKoin

@Composable
fun rememberPostTranslationState(blog: Blog): PostTranslationState {
    val translator: PostTranslator = getKoin().get()
    val textHandler: TextHandler = getKoin().get()
    val state = remember(blog, translator, textHandler) {
        PostTranslationState(translator, textHandler)
    }
    LaunchedEffect(blog, translator, state) {
        state.initStatus(blog)
    }
    return state
}

class PostTranslationState(
    private val postTranslator: PostTranslator,
    private val textHandler: TextHandler,
) {

    var status: PostTranslationStatus by mutableStateOf(PostTranslationStatus.Idle)
        private set

    var showTranslation by mutableStateOf(false)
        private set

    suspend fun initStatus(post: Blog) {
        postTranslator.getTranslateStatus(post.id)
            ?.collect { status = it }
    }

    suspend fun translatePost(blog: Blog) {
        if (postTranslator.isAiTranslateEnabled()) {
            showTranslation = true
            postTranslator.startTranslatePost(blog)
                .onEach { status = it }
                .first {
                    it is PostTranslationStatus.Translated || it is PostTranslationStatus.Failed
                }
        } else {
            val translatePlainText = postTranslator.buildTranslatePlainContent(blog)
            if (translatePlainText.isNullOrEmpty()) return
            textHandler.openSystemTranslateTextPage(translatePlainText)
        }
    }

    fun cancel(blog: Blog) {
        postTranslator.cancelTranslatePost(blog.id)
    }

    fun hideTranslation() {
        showTranslation = false
    }
}

sealed class PostTranslationStatus {

    data object Idle : PostTranslationStatus()

    data object Translating : PostTranslationStatus()

    data class Translated(
        val translatedContent: TranslatedPost,
    ) : PostTranslationStatus()

    data class Failed(val error: Throwable) : PostTranslationStatus()
}

class TranslatedPost(
    val spoiler: List<TranslatorBlock>?,
    val content: List<TranslatorBlock>?,
    val description: List<TranslatorBlock>?,
    val title: String?,
    val medias: List<MediaAltTranslatingContent>?,
    val poll: List<String>?,
) {

    val humanizedSpoilerText: RichText? by lazy {
        spoiler?.takeIf { it.isNotEmpty() }?.let { RichText.create(it) }
    }

    val humanizedContent: RichText? by lazy {
        content?.takeIf { it.isNotEmpty() }?.let { RichText.create(it) }
    }

    val humanizedDescription: RichText? by lazy {
        description?.takeIf { it.isNotEmpty() }?.let { RichText.create(it) }
    }
}
