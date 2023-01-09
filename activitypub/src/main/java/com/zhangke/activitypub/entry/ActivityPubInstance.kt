package com.zhangke.activitypub.entry

import com.google.gson.annotations.SerializedName

data class ActivityPubInstance(
    val domain: String,
    val title: String,
    val version: String,
    @SerializedName("source_url") val sourceUrl: String?,
    val description: String,
    val usage: Usage,
    val thumbnail: Thumbnail,
    val languages: List<String>,
    val rules: List<Rule>,
) {

    data class Usage(val users: Users) {

        data class Users(
            @SerializedName("active_month") val activeMonth: Int
        )
    }

    data class Thumbnail(val url: String)

    data class Rule(
        val id: String,
        val text: String,
    )
}
