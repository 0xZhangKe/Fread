package com.zhangke.activitypub.api

enum class ActivityPubScope(val scope: String) {

    READ("read"),

    WRITE("write"),

    FOLLOW("follow"),

    PUSH("push");

    companion object {

        val ALL = values()
    }
}