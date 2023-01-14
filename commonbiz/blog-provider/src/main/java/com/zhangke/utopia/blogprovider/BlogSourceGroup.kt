package com.zhangke.utopia.blogprovider

data class BlogSourceGroup(
    val metaSourceInfo: MetaSourceInfo,
    val sourceList: List<BlogSource>
)