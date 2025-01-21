package com.zhangke.fread.status.blog

sealed interface BlogEmbed {

    data class Link(
        val url: String,
        val title: String,
        val description: String,
        val video: Boolean = false,
        val authorName: String? = null,
        val authorUrl: String? = null,
        val providerName: String? = null,
        val providerUrl: String? = null,
        val html: String? = null,
        val width: Int? = null,
        val height: Int? = null,
        val image: String? = null,
        val embedUrl: String? = null,
        val blurhash: String? = null,
    ): BlogEmbed

    data class Blog(
        val blog: com.zhangke.fread.status.blog.Blog,
    ): BlogEmbed
}
