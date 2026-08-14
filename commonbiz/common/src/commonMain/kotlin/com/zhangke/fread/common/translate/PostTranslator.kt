package com.zhangke.fread.common.translate

import com.zhangke.fread.common.ai.LLMModelConfigsRepo
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.isActivityPub
import com.zhangke.fread.status.model.isBluesky
import com.zhangke.fread.status.richtext.model.RichLinkTarget
import com.zhangke.fread.status.richtext.translate.RichTextTranslatorParser

class PostTranslator(
    private val richTextTranslatorParser: RichTextTranslatorParser,
    private val freadConfigManager: FreadConfigManager,
    private val modelConfigRepo: LLMModelConfigsRepo,
) {

    companion object {

        private const val TRANSLATION_LABEL_MENTION = "{{MENTION}}"
        private const val TRANSLATION_LABEL_LINK = "{{LINK}}"
        private const val TRANSLATION_LABEL_EMOJI = "{{EMOJI}}"
        private const val TRANSLATION_LABEL_NEWLINE = "{{NEWLINE}}"
        private const val TRANSLATION_LABEL_HASHTAG = "{{HASHTAG}}"

    }

    suspend fun translateContent(blog: Blog): Result<String> {
        if (!isAiTranslateEnabled()) return Result.failure(IllegalStateException("AI translate is not enabled"))
        val blockList = if (blog.platform.protocol.isActivityPub) {
            richTextTranslatorParser.parseHtml(
                html = blog.content,
                emojis = blog.emojis,
                hashTags = blog.tags,
                mentions = blog.mentions,
            )
        } else if (blog.platform.protocol.isBluesky) {
            richTextTranslatorParser.parseFacet(
                text = blog.content,
                facets = blog.facets,
            )
        } else {
            richTextTranslatorParser.parseHtml(blog.content)
        }

        return Result.success("")
    }

    private fun buildTranslateContent(blockList: List<RichTextTranslatorParser.TranslatorBlock>): String {
        return blockList.joinToString(separator = "") { block ->
            when (block) {
                is RichTextTranslatorParser.TranslatorBlock.PlainTextBlock -> block.text
                is RichTextTranslatorParser.TranslatorBlock.LinkBlock -> {
                    when (block.target) {
                        is RichLinkTarget.HashtagTarget, is RichLinkTarget.MaybeHashtagTarget -> TRANSLATION_LABEL_HASHTAG
                        is RichLinkTarget.MentionTarget, is RichLinkTarget.MentionDidTarget -> TRANSLATION_LABEL_MENTION
                        else -> TRANSLATION_LABEL_LINK
                    }
                }

                is RichTextTranslatorParser.TranslatorBlock.NewLineBlock -> TRANSLATION_LABEL_NEWLINE
                is RichTextTranslatorParser.TranslatorBlock.EmojiBlock -> TRANSLATION_LABEL_EMOJI
            }
        }
    }

    suspend fun isAiTranslateEnabled(): Boolean {
        val aiTranslateEnabled = freadConfigManager.getAiTranslateEnabled()
        if (!aiTranslateEnabled) return false
        modelConfigRepo.getAllProvider().firstOrNull { it.selected } ?: return false
        val lan = freadConfigManager.getTranslateTargetLanguage()
        return !lan.isNullOrBlank()
    }
}
