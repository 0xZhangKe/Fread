package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubPostStatusRequestEntity
import com.zhangke.activitypub.entities.ActivityPubQuoteApprovalEntity
import com.zhangke.fread.status.model.QuoteApprovalPolicy

val QuoteApprovalPolicy.apCode: String
    get() = when (this) {
        QuoteApprovalPolicy.PUBLIC -> ActivityPubPostStatusRequestEntity.QUOTE_APPROVAL_POLICY_PUBLIC
        QuoteApprovalPolicy.FOLLOWERS -> ActivityPubPostStatusRequestEntity.QUOTE_APPROVAL_POLICY_FOLLOWERS
        QuoteApprovalPolicy.FOLLOWING -> ActivityPubPostStatusRequestEntity.QUOTE_APPROVAL_POLICY_PUBLIC
        QuoteApprovalPolicy.NOBODY -> ActivityPubPostStatusRequestEntity.QUOTE_APPROVAL_POLICY_NOBODY
    }

fun String.toQuoteApprovalPolicy(): QuoteApprovalPolicy = when (this) {
    ActivityPubPostStatusRequestEntity.QUOTE_APPROVAL_POLICY_PUBLIC -> QuoteApprovalPolicy.PUBLIC
    ActivityPubPostStatusRequestEntity.QUOTE_APPROVAL_POLICY_FOLLOWERS -> QuoteApprovalPolicy.FOLLOWERS
    ActivityPubPostStatusRequestEntity.QUOTE_APPROVAL_POLICY_NOBODY -> QuoteApprovalPolicy.NOBODY
    else -> QuoteApprovalPolicy.PUBLIC
}
