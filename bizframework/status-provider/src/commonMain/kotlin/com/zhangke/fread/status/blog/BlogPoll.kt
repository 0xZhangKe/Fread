package com.zhangke.fread.status.blog

import com.zhangke.framework.utils.PlatformSerializable
import kotlinx.serialization.Serializable

@Serializable
data class BlogPoll(
    val id: String,
    /**
     * ISO 8601 Datetime
     */
    val expiresAt: String?,
    val expired: Boolean,
    /**
     * Does the poll allow multiple-choice answers?
     */
    val multiple: Boolean,
    /**
     * How many votes have been received.
     */
    val votesCount: Int,
    /**
     * How many unique accounts have voted on a multiple-choice poll.
     */
    val votersCount: Int,
    val options: List<Option>,
    val voted: Boolean?,
    val ownVotes: List<Int>,
): PlatformSerializable {

    @Serializable
    data class Option(
        val index: Int,
        val title: String,
        val votesCount: Int?,
    ): PlatformSerializable
}
