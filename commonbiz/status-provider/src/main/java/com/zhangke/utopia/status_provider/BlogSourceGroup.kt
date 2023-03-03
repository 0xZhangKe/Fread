package com.zhangke.utopia.status_provider

data class BlogSourceGroup(
    val metaSourceInfo: MetaSourceInfo,
    val sourceList: List<StatusSource>
)