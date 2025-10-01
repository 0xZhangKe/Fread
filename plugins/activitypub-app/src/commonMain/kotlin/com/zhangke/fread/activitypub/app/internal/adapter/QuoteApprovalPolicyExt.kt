package com.zhangke.fread.activitypub.app.internal.adapter

import com.zhangke.activitypub.entities.ActivityPubQuoteApprovalEntity
import com.zhangke.fread.status.model.QuoteApprovalPolicy

val QuoteApprovalPolicy.apCode: String
    get() = when (this) {
        QuoteApprovalPolicy.PUBLIC -> ActivityPubQuoteApprovalEntity.PUBLIC
        QuoteApprovalPolicy.FOLLOWERS -> ActivityPubQuoteApprovalEntity.FOLLOWERS
        QuoteApprovalPolicy.FOLLOWING -> ActivityPubQuoteApprovalEntity.FOLLOWING
        QuoteApprovalPolicy.NOBODY -> ActivityPubQuoteApprovalEntity.UNSUPPORTED_POLICY
    }
