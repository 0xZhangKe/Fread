package com.zhangke.utopia.activitypub.app.internal.uri

import com.zhangke.utopia.status.uri.FormalUri

val FormalUri.isActivityPubUri: Boolean get() = host == ACTIVITY_PUB_HOST
