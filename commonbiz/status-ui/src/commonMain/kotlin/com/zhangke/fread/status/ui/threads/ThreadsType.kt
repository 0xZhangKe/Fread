package com.zhangke.fread.status.ui.threads

enum class ThreadsType {

    UNSPECIFIED,

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

    /**
     * 在 Feeds 中的有父级的帖子
     */
    CONTINUED_THREAD,
}
