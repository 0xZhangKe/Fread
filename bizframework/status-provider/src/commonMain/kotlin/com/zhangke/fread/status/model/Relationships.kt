package com.zhangke.fread.status.model

import kotlinx.serialization.Serializable

@Serializable
data class Relationships(
    val blocking: Boolean,
    val blockedBy: Boolean,
    val following: Boolean,
    val followedBy: Boolean,
    val muting: Boolean,
    val requested: Boolean?,
    val requestedBy: Boolean?,
) {

    companion object {

        fun default(
            blocking: Boolean = false,
            blockedBy: Boolean = false,
            following: Boolean = false,
            followedBy: Boolean = false,
            muting: Boolean = false,
            requested: Boolean? = null,
            requestedBy: Boolean? = null,
        ): Relationships {
            return Relationships(
                blocking = blocking,
                blockedBy = blockedBy,
                following = following,
                followedBy = followedBy,
                muting = muting,
                requested = requested,
                requestedBy = requestedBy,
            )
        }
    }
}
