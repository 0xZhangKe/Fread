package com.zhangke.fread.status.ui.threads

enum class ThreadsType {

    NONE,

    /**
     * 第一个评论
     */
    FIRST_ANCESTOR,

    /**
     * 帖子上级评论
     */
    ANCESTOR,

    /**
     * 锚点帖子，且没有父级
     */
    ANCHOR_FIRST,

    /**
     * 锚点帖子
     */
    ANCHOR,
}
