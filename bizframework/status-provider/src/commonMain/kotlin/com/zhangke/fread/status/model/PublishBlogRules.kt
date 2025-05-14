package com.zhangke.fread.status.model

data class PublishBlogRules(
    val maxCharacters: Int,
    val maxMediaCount: Int,
    val maxPollOptions: Int,
    val supportSpoiler: Boolean,
    val supportPoll: Boolean,
    val maxLanguageCount: Int,
)
