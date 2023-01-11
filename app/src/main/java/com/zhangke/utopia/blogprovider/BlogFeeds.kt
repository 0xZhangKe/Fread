package com.zhangke.utopia.blogprovider

data class BlogFeeds(
    val id: Int,
    val name: String,
    val sourceList: List<BlogSource>
)

data class BlogFeedsShell(
    val id: Int,
    val name: String,
    val sourceIdList: List<String>
)