package com.zhangke.fread.activitypub.app.internal.uri

import com.zhangke.fread.status.uri.FormalUri

val FormalUri.isActivityPubUri: Boolean get() = host == ACTIVITY_PUB_HOST
