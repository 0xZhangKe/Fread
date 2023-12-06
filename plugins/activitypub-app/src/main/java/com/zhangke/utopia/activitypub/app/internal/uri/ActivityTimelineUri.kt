package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType

//class ActivityTimelineUri private constructor(
//    val timelineServerHost: String,
//    val type: TimelineSourceType,
//    queries: Map<String, String>
//) : ActivityPubUri(PATH, queries) {
//
//    companion object {
//
//        const val PATH = "/timeline"
//
//        internal const val QUERY_HOST = "host"
//        internal const val QUERY_TYPE = "type"
//
//        fun create(host: String, type: TimelineSourceType): ActivityTimelineUri {
//            val queries = mapOf(QUERY_HOST to host, QUERY_TYPE to type.stringValue)
//            return ActivityTimelineUri(host, type, queries)
//        }
//    }
//}
